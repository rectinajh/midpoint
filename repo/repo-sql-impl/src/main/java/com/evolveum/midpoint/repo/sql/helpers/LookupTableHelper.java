/*
 * Copyright (c) 2010-2015 Evolveum
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.evolveum.midpoint.repo.sql.helpers;

import com.evolveum.midpoint.prism.PrismContainerValue;
import com.evolveum.midpoint.prism.PrismContext;
import com.evolveum.midpoint.prism.PrismObject;
import com.evolveum.midpoint.prism.delta.ContainerDelta;
import com.evolveum.midpoint.prism.delta.ItemDelta;
import com.evolveum.midpoint.prism.path.ItemPath;
import com.evolveum.midpoint.prism.path.NameItemPathSegment;
import com.evolveum.midpoint.prism.polystring.PolyString;
import com.evolveum.midpoint.prism.polystring.PrismDefaultPolyStringNormalizer;
import com.evolveum.midpoint.prism.query.ObjectPaging;
import com.evolveum.midpoint.repo.sql.data.common.RLookupTable;
import com.evolveum.midpoint.repo.sql.data.common.RObject;
import com.evolveum.midpoint.repo.sql.data.common.other.RLookupTableRow;
import com.evolveum.midpoint.repo.sql.util.RUtil;
import com.evolveum.midpoint.schema.GetOperationOptions;
import com.evolveum.midpoint.schema.ObjectSelector;
import com.evolveum.midpoint.schema.RelationalValueSearchQuery;
import com.evolveum.midpoint.schema.SelectorOptions;
import com.evolveum.midpoint.util.exception.SchemaException;
import com.evolveum.midpoint.util.logging.Trace;
import com.evolveum.midpoint.util.logging.TraceManager;
import com.evolveum.midpoint.xml.ns._public.common.common_3.LookupTableRowType;
import com.evolveum.midpoint.xml.ns._public.common.common_3.LookupTableType;
import com.evolveum.midpoint.xml.ns._public.common.common_3.ObjectType;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Contains methods specific to handle LookupTable rows.
 * (As these rows are stored outside main LookupTable object.)
 *
 * It is quite a temporary solution in order to ease SqlRepositoryServiceImpl
 * from tons of type-specific code. Serious solution would be to implement
 * subobject-level operations more generically.
 *
 * @author lazyman, mederly
 */
@Component
public class LookupTableHelper {

    private static final Trace LOGGER = TraceManager.getTrace(LookupTableHelper.class);

    @Autowired
    private GeneralHelper generalHelper;

    public void addLookupTableRows(Session session, RObject object, boolean merge) {
        if (!(object instanceof RLookupTable)) {
            return;
        }
        RLookupTable table = (RLookupTable) object;

        if (merge) {
            deleteLookupTableRows(session, table.getOid());
        }
        if (table.getRows() != null) {
            for (RLookupTableRow row : table.getRows()) {
                session.save(row);
            }
        }
    }

    public void addLookupTableRows(Session session, String tableOid, Collection<PrismContainerValue> values, int currentId) {
        for (PrismContainerValue value : values) {
            LookupTableRowType rowType = new LookupTableRowType();
            rowType.setupContainerValue(value);

            RLookupTableRow row = RLookupTableRow.toRepo(tableOid, rowType);
            row.setId(currentId);
            currentId++;
            session.save(row);
        }
    }

    /**
     * This method removes all lookup table rows for object defined by oid
     * @param session
     * @param oid
     */
    public void deleteLookupTableRows(Session session, String oid) {
        Query query = session.getNamedQuery("delete.lookupTableData");
        query.setParameter("oid", oid);

        query.executeUpdate();
    }

    public void updateLookupTableData(Session session, RObject object, Collection<? extends ItemDelta> modifications) {
        if (modifications.isEmpty()) {
            return;
        }

        if (!(object instanceof RLookupTable)) {
            throw new IllegalStateException("Object being modified is not a LookupTable; it is " + object.getClass());
        }
        final RLookupTable rLookupTable = (RLookupTable) object;
        final String tableOid = object.getOid();

        for (ItemDelta delta : modifications) {
            if (!(delta instanceof ContainerDelta) || delta.getPath().size() != 1) {
                throw new IllegalStateException("Wrong table delta sneaked into updateLookupTableData: class=" + delta.getClass() + ", path=" + delta.getPath());
            }
            // one "table" container modification
            ContainerDelta containerDelta = (ContainerDelta) delta;

            if (containerDelta.getValuesToDelete() != null) {
                // todo do 'bulk' delete like delete from ... where oid=? and id in (...)
                for (PrismContainerValue value : (Collection<PrismContainerValue>) containerDelta.getValuesToDelete()) {
                    Query query = session.getNamedQuery("delete.lookupTableDataRow");
                    query.setString("oid", tableOid);
                    query.setInteger("id", RUtil.toInteger(value.getId()));
                    query.executeUpdate();
                }
            }
            if (containerDelta.getValuesToAdd() != null) {
                int currentId = generalHelper.findLastIdInRepo(session, tableOid, "get.lookupTableLastId") + 1;
                addLookupTableRows(session, tableOid, containerDelta.getValuesToAdd(), currentId);
            }
            if (containerDelta.getValuesToReplace() != null) {
                deleteLookupTableRows(session, tableOid);
                addLookupTableRows(session, tableOid, containerDelta.getValuesToReplace(), 1);
            }
        }
    }

    public GetOperationOptions findLookupTableGetOption(Collection<SelectorOptions<GetOperationOptions>> options) {
        final ItemPath tablePath = new ItemPath(LookupTableType.F_ROW);

        Collection<SelectorOptions<GetOperationOptions>> filtered = SelectorOptions.filterRetrieveOptions(options);
        for (SelectorOptions<GetOperationOptions> option : filtered) {
            ObjectSelector selector = option.getSelector();
            if (selector == null) {
            	// Ignore this. These are top-level options. There will not
            	// apply to lookup table
            	continue;
            }
            ItemPath selected = selector.getPath();

            if (tablePath.equivalent(selected)) {
                return option.getOptions();
            }
        }

        return null;
    }

    public <T extends ObjectType> void updateLoadedLookupTable(PrismObject<T> object,
                                                               Collection<SelectorOptions<GetOperationOptions>> options,
                                                               Session session) throws SchemaException {
        if (!SelectorOptions.hasToLoadPath(LookupTableType.F_ROW, options)) {
            return;
        }

        LOGGER.debug("Loading lookup table data.");

        GetOperationOptions getOption = findLookupTableGetOption(options);
        RelationalValueSearchQuery queryDef = getOption == null ? null : getOption.getRelationalValueSearchQuery();
        Criteria criteria = setupLookupTableRowsQuery(session, queryDef, object.getOid());
        if (queryDef != null && queryDef.getPaging() != null) {
            ObjectPaging paging = queryDef.getPaging();

            if (paging.getOffset() != null) {
                criteria.setFirstResult(paging.getOffset());
            }
            if (paging.getMaxSize() != null) {
                criteria.setMaxResults(paging.getMaxSize());
            }

            ItemPath orderByPath = paging.getOrderBy();
            if (paging.getDirection() != null && orderByPath != null && !orderByPath.isEmpty()) {
                if (orderByPath.size() > 1 || !(orderByPath.first() instanceof NameItemPathSegment)) {
                    throw new SchemaException("OrderBy has to consist of just one naming segment");
                }
                String orderBy = ((NameItemPathSegment) (orderByPath.first())).getName().getLocalPart();
                switch (paging.getDirection()) {
                    case ASCENDING:
                        criteria.addOrder(Order.asc(orderBy));
                        break;
                    case DESCENDING:
                        criteria.addOrder(Order.desc(orderBy));
                        break;
                }
            }
        }

        List<RLookupTableRow> rows = criteria.list();
        if (rows == null || rows.isEmpty()) {
            return;
        }

        LookupTableType lookup = (LookupTableType) object.asObjectable();
        List<LookupTableRowType> jaxbRows = lookup.getRow();
        for (RLookupTableRow row : rows) {
            LookupTableRowType jaxbRow = row.toJAXB();
            jaxbRows.add(jaxbRow);
        }
    }

    private Criteria setupLookupTableRowsQuery(Session session, RelationalValueSearchQuery queryDef, String oid) {
        Criteria criteria = session.createCriteria(RLookupTableRow.class);
        criteria.add(Restrictions.eq("ownerOid", oid));

        if (queryDef != null
                && queryDef.getColumn() != null
                && queryDef.getSearchType() != null
                && StringUtils.isNotEmpty(queryDef.getSearchValue())) {

            String param = queryDef.getColumn().getLocalPart();
            String value = queryDef.getSearchValue();
            if (LookupTableRowType.F_LABEL.equals(queryDef.getColumn())) {
                param = "label.norm";

                PolyString poly = new PolyString(value);
                poly.recompute(new PrismDefaultPolyStringNormalizer());
                value = poly.getNorm();
            }
            switch (queryDef.getSearchType()) {
                case EXACT:
                    criteria.add(Restrictions.eq(param, value));
                    break;
                case STARTS_WITH:
                    criteria.add(Restrictions.like(param, value + "%"));
                    break;
                case SUBSTRING:
                    criteria.add(Restrictions.like(param, "%" + value + "%"));
            }
        }

        return criteria;
    }

    public <T extends ObjectType> Collection<? extends ItemDelta> filterLookupTableModifications(Class<T> type,
                                                                                                 Collection<? extends ItemDelta> modifications) {
        Collection<ItemDelta> tableDelta = new ArrayList<>();
        if (!LookupTableType.class.equals(type)) {
            return tableDelta;
        }

        ItemPath tablePath = new ItemPath(LookupTableType.F_ROW);
        for (ItemDelta delta : modifications) {
            ItemPath path = delta.getPath();
            if (path.isEmpty()) {
                throw new UnsupportedOperationException("Lookup table cannot be modified via empty-path modification");
            } else if (path.equivalent(tablePath)) {
                tableDelta.add(delta);
            } else if (path.isSuperPath(tablePath)) {
                // todo - what about modifications with path like table[id] or table[id]/xxx where xxx=key|value|label?
                throw new UnsupportedOperationException("Lookup table row can be modified only by specifying path=table");
            }
        }

        modifications.removeAll(tableDelta);

        return tableDelta;
    }
}
