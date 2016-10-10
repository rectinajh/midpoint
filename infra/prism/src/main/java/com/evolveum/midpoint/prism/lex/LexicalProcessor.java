/*
 * Copyright (c) 2014 Evolveum
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
 package com.evolveum.midpoint.prism.lex;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import javax.xml.namespace.QName;

import com.evolveum.midpoint.prism.ParserSource;
import com.evolveum.midpoint.prism.ParsingContext;
import com.evolveum.midpoint.prism.SerializationContext;
import com.evolveum.midpoint.prism.xnode.RootXNode;
import com.evolveum.midpoint.prism.xnode.XNode;
import com.evolveum.midpoint.util.exception.SchemaException;

/**
 * @author semancik
 *
 */
public interface LexicalProcessor {

	XNode read(ParserSource source, ParsingContext parsingContext) throws SchemaException, IOException;

	Collection<XNode> readCollection(ParserSource source, ParsingContext parsingContext) throws SchemaException, IOException;
	
	boolean canRead(File file) throws IOException;
	
	boolean canRead(String dataString);

	String write(XNode xnode, QName rootElementName, SerializationContext serializationContext) throws SchemaException;
	
	String write(RootXNode xnode, SerializationContext serializationContext) throws SchemaException;

}
