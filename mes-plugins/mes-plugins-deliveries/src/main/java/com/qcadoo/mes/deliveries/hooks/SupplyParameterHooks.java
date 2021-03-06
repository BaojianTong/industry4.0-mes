/**
 * ***************************************************************************
 * Copyright (c) 2018 RiceFish Limited
 * Project: SmartMES
 * Version: 1.6
 *
 * This file is part of SmartMES.
 *
 * SmartMES is Authorized software; you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation; either version 3 of the License,
 * or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * ***************************************************************************
 */
package com.qcadoo.mes.deliveries.hooks;

import static com.qcadoo.mes.deliveries.constants.DefaultAddressType.OTHER;
import static com.qcadoo.mes.deliveries.constants.ParameterFieldsD.DEFAULT_ADDRESS;
import static com.qcadoo.mes.deliveries.constants.ParameterFieldsD.LOCATION;
import static com.qcadoo.mes.deliveries.constants.ParameterFieldsD.OTHER_ADDRESS;
import static com.qcadoo.mes.materialFlow.constants.LocationFields.TYPE;

import org.springframework.stereotype.Service;

import com.qcadoo.mes.deliveries.constants.ParameterFieldsD;
import com.qcadoo.mes.materialFlow.constants.LocationType;
import com.qcadoo.model.api.DataDefinition;
import com.qcadoo.model.api.Entity;
import com.qcadoo.view.api.ComponentState;
import com.qcadoo.view.api.ViewDefinitionState;
import com.qcadoo.view.api.components.FieldComponent;

@Service
public class SupplyParameterHooks {

    public void setFieldsVisibleAndRequired(final ViewDefinitionState view, final ComponentState componentState,
            final String[] args) {
        setFieldsVisibleAndRequired(view);
    }

    public void setFieldsVisibleAndRequired(final ViewDefinitionState view) {
        FieldComponent defaultAddress = (FieldComponent) view.getComponentByReference(DEFAULT_ADDRESS);

        boolean selectForAddress = OTHER.getStringValue().equals(defaultAddress.getFieldValue());

        changeFieldsState(view, OTHER_ADDRESS, selectForAddress);
    }

    private void changeFieldsState(final ViewDefinitionState view, final String fieldName, final boolean selectForAddress) {
        FieldComponent field = (FieldComponent) view.getComponentByReference(fieldName);
        field.setVisible(selectForAddress);
        field.setRequired(selectForAddress);

        if (!selectForAddress) {
            field.setFieldValue(null);
        }

        field.requestComponentUpdateState();
    }

    public boolean checkIfLocationIsWarehouse(final DataDefinition parameterDD, final Entity parameter) {
        Entity location = parameter.getBelongsToField(LOCATION);

        if ((location != null) && !isLocationIsWarehouse(location)) {
            parameter.addError(parameterDD.getField(LOCATION), "parameter.validate.global.error.locationIsNotWarehouse");
            return false;
        }
        return true;
    }

    private boolean isLocationIsWarehouse(final Entity location) {
        return ((location != null) && LocationType.WAREHOUSE.getStringValue().equals(location.getStringField(TYPE)));
    }

    public void onCreate(final DataDefinition dataDefinition, final Entity parameter) {

        parameter.setField(ParameterFieldsD.DELIVERED_BIGGER_THAN_ORDERED, true);
    }

}
