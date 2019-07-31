/**
 * Copyright (c) 2018-2019 Evolveum
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
package com.evolveum.midpoint.test.asserter;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertNull;
import static org.testng.AssertJUnit.assertTrue;

import java.util.Collection;

import com.evolveum.midpoint.prism.Containerable;
import com.evolveum.midpoint.prism.PrismContainerValue;
import com.evolveum.midpoint.prism.PrismPropertyValue;
import com.evolveum.midpoint.prism.delta.ContainerDelta;
import com.evolveum.midpoint.prism.delta.PropertyDelta;
import com.evolveum.midpoint.test.asserter.prism.PrismContainerValueSetAsserter;
import com.evolveum.midpoint.test.asserter.prism.PrismPropertyValueSetAsserter;

/**
 * @author semancik
 *
 */
public class PropertyDeltaAsserter<T,RA> extends AbstractAsserter<RA> {
	
	private PropertyDelta<T> propertyDelta;

	public PropertyDeltaAsserter(PropertyDelta<T> propertyDelta) {
		super();
		this.propertyDelta = propertyDelta;
	}
	
	public PropertyDeltaAsserter(PropertyDelta<T> propertyDelta, String detail) {
		super(detail);
		this.propertyDelta = propertyDelta;
	}
	
	public PropertyDeltaAsserter(PropertyDelta<T> propertyDelta, RA returnAsserter, String detail) {
		super(returnAsserter, detail);
		this.propertyDelta = propertyDelta;
	}
	
	public static <T> PropertyDeltaAsserter<T,Void> forDelta(PropertyDelta<T> propertyDelta) {
		return new PropertyDeltaAsserter<>(propertyDelta);
	}
	
	public PrismPropertyValueSetAsserter<T,PropertyDeltaAsserter<T,RA>> valuesToAdd() {
		Collection<PrismPropertyValue<T>> valuesToAdd = propertyDelta.getValuesToAdd();
		PrismPropertyValueSetAsserter<T,PropertyDeltaAsserter<T,RA>> setAsserter = new PrismPropertyValueSetAsserter<>(
				valuesToAdd, this, "values to add in "+desc());
		copySetupTo(setAsserter);
		return setAsserter;
	}
	
	public PropertyDeltaAsserter<T,RA> assertNoValuesToAdd() {
		Collection<PrismPropertyValue<T>> valuesToAdd = propertyDelta.getValuesToAdd();
		assertNull("Unexpected values to add in "+desc()+": "+valuesToAdd, valuesToAdd);
		return this;
	}

	public PrismPropertyValueSetAsserter<T,PropertyDeltaAsserter<T,RA>> valuesToDelete() {
		Collection<PrismPropertyValue<T>> valuesToDelete = propertyDelta.getValuesToDelete();
		PrismPropertyValueSetAsserter<T,PropertyDeltaAsserter<T,RA>> setAsserter = new PrismPropertyValueSetAsserter<>(
				valuesToDelete, this, "values to delte in "+desc());
		copySetupTo(setAsserter);
		return setAsserter;
	}
	
	public PropertyDeltaAsserter<T,RA> assertNoValuesToDelete() {
		Collection<PrismPropertyValue<T>> valuesToDelete = propertyDelta.getValuesToDelete();
		assertNull("Unexpected values to delete in "+desc()+": "+valuesToDelete, valuesToDelete);
		return this;
	}
	
	public PrismPropertyValueSetAsserter<T,PropertyDeltaAsserter<T,RA>> valuesToReplace() {
		Collection<PrismPropertyValue<T>> valuesToReplace = propertyDelta.getValuesToReplace();
		PrismPropertyValueSetAsserter<T,PropertyDeltaAsserter<T,RA>> setAsserter = new PrismPropertyValueSetAsserter<>(
				valuesToReplace, this, "values to replace in "+desc());
		copySetupTo(setAsserter);
		return setAsserter;
	}
	
	public PropertyDeltaAsserter<T,RA> assertNoValuesToReplace() {
		Collection<PrismPropertyValue<T>> valuesToReplace = propertyDelta.getValuesToReplace();
		assertNull("Unexpected values to replace in "+desc()+": "+valuesToReplace, valuesToReplace);
		return this;
	}
	
	protected String desc() {
		return descWithDetails(propertyDelta);
	}

}
