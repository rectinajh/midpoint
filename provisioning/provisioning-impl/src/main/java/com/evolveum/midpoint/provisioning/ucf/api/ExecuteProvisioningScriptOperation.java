/*
 * Copyright (c) 2010-2013 Evolveum
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
package com.evolveum.midpoint.provisioning.ucf.api;

import java.util.ArrayList;
import java.util.List;

import com.evolveum.midpoint.schema.util.SchemaDebugUtil;
import com.evolveum.midpoint.xml.ns._public.common.common_2a.ProvisioningScriptOrderType;

/**
 * 
 * @author Radovan Semancik
 */
public class ExecuteProvisioningScriptOperation extends Operation {

	private static final int DEBUG_MAX_CODE_LENGTH = 32;

	private List<ExecuteScriptArgument> argument;

	private boolean connectorHost;
	private boolean resourceHost;

	private String textCode;
	private String language;

	private ProvisioningScriptOrderType scriptOrder;
	
	public ExecuteProvisioningScriptOperation() {

	}

	public List<ExecuteScriptArgument> getArgument() {
		if (argument == null){
			argument = new ArrayList<ExecuteScriptArgument>();
		}
		return argument;
	}

	public boolean isConnectorHost() {
		return connectorHost;
	}

	public void setConnectorHost(boolean connectorHost) {
		this.connectorHost = connectorHost;
	}

	public boolean isResourceHost() {
		return resourceHost;
	}

	public void setResourceHost(boolean resourceHost) {
		this.resourceHost = resourceHost;
	}

	public String getTextCode() {
		return textCode;
	}

	public void setTextCode(String textCode) {
		this.textCode = textCode;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public ProvisioningScriptOrderType getScriptOrder() {
		return scriptOrder;
	}

	public void setScriptOrder(ProvisioningScriptOrderType scriptOrder) {
		this.scriptOrder = scriptOrder;
	}

	@Override
	public String debugDump(int indent) {
		StringBuilder sb = new StringBuilder();
		SchemaDebugUtil.indentDebugDump(sb, indent);
		sb.append("Script execution ");
		sb.append(toStringInternal());
		return sb.toString();
	}

	@Override
	public String toString() {
		return "ExecuteProvisioningScriptOperation(" + toStringInternal() + ")";
	}
	
	private String toStringInternal() {
		StringBuilder sb = new StringBuilder();
		if (connectorHost) {
			sb.append("on connector ");
		}
		if (resourceHost) {
			sb.append("on resource ");
		}
		sb.append(scriptOrder);
		sb.append(" : ");
		if (textCode.length() <= DEBUG_MAX_CODE_LENGTH) {
			sb.append(textCode);
		} else {
			sb.append(textCode.substring(0, DEBUG_MAX_CODE_LENGTH));
			sb.append(" ...(truncated)...");
		}
		return sb.toString();
	}
}
