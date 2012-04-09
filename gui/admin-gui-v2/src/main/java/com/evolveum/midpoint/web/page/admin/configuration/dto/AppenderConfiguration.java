/*
 * Copyright (c) 2012 Evolveum
 *
 * The contents of this file are subject to the terms
 * of the Common Development and Distribution License
 * (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at
 * http://www.opensource.org/licenses/cddl1 or
 * CDDLv1.0.txt file in the source code distribution.
 * See the License for the specific language governing
 * permission and limitations under the License.
 *
 * If applicable, add the following below the CDDL Header,
 * with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 *
 * Portions Copyrighted 2012 [name of copyright owner]
 */

package com.evolveum.midpoint.web.page.admin.configuration.dto;

import com.evolveum.midpoint.web.component.util.Selectable;
import com.evolveum.midpoint.xml.ns._public.common.common_1.AppenderConfigurationType;
import org.apache.commons.lang.Validate;

/**
 * @author lazyman
 */
public class AppenderConfiguration<T extends AppenderConfigurationType> extends Selectable implements Comparable<T> {

    private T config;

    public AppenderConfiguration(T config) {
        Validate.notNull(config, "Appender configuration type must not be null.");
        this.config = config;
    }

    protected T getConfig() {
        return config;
    }

    public String getPattern() {
        return config.getPattern();
    }

    public void setPattern(String pattern) {
        config.setPattern(pattern);
    }

    public void setName(String name) {
        config.setName(name);
    }

    public String getName() {
        return config.getName();
    }

    public String getFilePath() {
        return null;
    }

    public String getFilePattern() {
        return null;
    }

    public Integer getMaxHistory() {
        return null;
    }

    public String getMaxFileSize() {
        return null;
    }

    public boolean appending() {
        return false;
    }

    @Override
    public int compareTo(T t) {
        if (t == null) {
            return 0;
        }
        return String.CASE_INSENSITIVE_ORDER.compare(getName(), t.getName());
    }
}
