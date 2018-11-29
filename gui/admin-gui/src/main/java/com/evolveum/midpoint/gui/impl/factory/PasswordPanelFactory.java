/*
 * Copyright (c) 2010-2018 Evolveum
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.evolveum.midpoint.gui.impl.factory;

import javax.annotation.PostConstruct;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.evolveum.midpoint.gui.api.component.password.PasswordPanel;
import com.evolveum.midpoint.gui.api.factory.AbstractGuiComponentFactory;
import com.evolveum.midpoint.gui.api.registry.GuiComponentRegistry;
import com.evolveum.midpoint.web.component.prism.ValueWrapper;
import com.evolveum.midpoint.web.page.admin.users.PageUser;
import com.evolveum.prism.xml.ns._public.types_3.ProtectedStringType;

/**
 * @author katka
 *
 */
@Component
public class PasswordPanelFactory extends AbstractGuiComponentFactory {

	@Autowired private GuiComponentRegistry registry;
	
	@PostConstruct
	public void register() {
		registry.addToRegistry(this);
	}
	
	@Override
	public <T> boolean match(ValueWrapper<T> valueWrapper) {
		return ProtectedStringType.COMPLEX_TYPE.equals(valueWrapper.getItem().getItemDefinition().getTypeName());
	}

	@Override
	public <T> Panel createPanel(PanelContext<T> panelCtx) {
		
		if (!(panelCtx.getParentComponent().getPage() instanceof PageUser)) {
			return new PasswordPanel(panelCtx.getComponentId(), (IModel<ProtectedStringType>) panelCtx.getRealValueModel(),
				panelCtx.getBaseModel().getObject().isReadonly(), true);
		} 
		
		return new PasswordPanel(panelCtx.getComponentId(), (IModel<ProtectedStringType>) panelCtx.getRealValueModel(),
				panelCtx.getBaseModel().getObject().isReadonly());
		
	}

	
	
}
