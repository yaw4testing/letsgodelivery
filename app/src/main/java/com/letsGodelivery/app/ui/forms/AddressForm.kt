package com.letsGodelivery.app.ui.forms

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.letsGodelivery.app.data.models.Address

@Composable
fun AddressForm(address: Address, onAddressChange: (Address) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = address.streetLine1.orEmpty(),
            onValueChange = { onAddressChange(address.copy(streetLine1 = it)) },
            label = { Text("Street Line 1") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = address.streetLine2.orEmpty(),
            onValueChange = { onAddressChange(address.copy(streetLine2 = it)) },
            label = { Text("Street Line 2") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = address.city.orEmpty(),
            onValueChange = { onAddressChange(address.copy(city = it)) },
            label = { Text("City") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = address.stateOrProvince.orEmpty(),
            onValueChange = { onAddressChange(address.copy(stateOrProvince = it)) },
            label = { Text("State/Province") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = address.postalCode.orEmpty(),
            onValueChange = { onAddressChange(address.copy(postalCode = it)) },
            label = { Text("Postal Code") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = address.country.orEmpty(),
            onValueChange = { onAddressChange(address.copy(country = it)) },
            label = { Text("Country") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
