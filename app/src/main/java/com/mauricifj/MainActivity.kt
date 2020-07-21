package com.mauricifj

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import br.com.braspag.verify_card.VerifyCard
import br.com.braspag.verify_card.VerifyCardEnvironment
import br.com.braspag.verify_card.models.Card
import br.com.braspag.verify_card.models.VerifyCardRequest
import br.com.braspag.verify_card.models.cardType
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val verifyCard = VerifyCard(
                clientId = "CLIENT-ID",
                clientSecret = "CLIENT-SECRET",
                merchantId = "MERCHANT-ID",
                environment = VerifyCardEnvironment.SANDBOX
        )

        card_expiration_month.setAdapter(ArrayAdapter(this, android.R.layout.simple_list_item_1, listOf("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12")))
        card_expiration_year.setAdapter(ArrayAdapter(this, android.R.layout.simple_list_item_1, listOf("2020", "2021", "2022", "2023", "2024", "2025", "2026", "2027", "2028", "2029", "2030")))
        card_brand.setAdapter(ArrayAdapter(this, android.R.layout.simple_list_item_1, listOf("Visa", "Master", "Elo")))

        verify.setOnClickListener {
            form.visibility = View.INVISIBLE
            loading.visibility = View.VISIBLE

            verifyCard.verify(
                request = VerifyCardRequest(
                    provider = "Cielo30",
                    card = Card(
                        cardNumber = card_number.text.toString(),
                        holder = holder.text.toString(),
                        securityCode = card_security_code.text.toString(),
                        expirationDate = "${card_expiration_month.text}/${card_expiration_year.text}",
                        brand = card_brand.text.toString(),
                        type = cardType.DEBIT_CARD
                    )
                )
            ) {
                if (it.errors.isNotEmpty()) {
                    errors_list_view.adapter = ArrayAdapter(
                        this,
                        android.R.layout.simple_list_item_1,
                        it.errors
                    )
                    errors_list_view.visibility = View.VISIBLE
                    backToForm.visibility = View.VISIBLE
                } else {
                    errors_list_view.visibility = View.GONE
                }
                if (it.result != null) {
                    with(it.result!!) {
                        result_status.text = status.toString()
                        result_provider_return_code.text = providerReturnCode
                        result_provider_return_message.text = providerReturnMessage
                        result_bin_data_provider.text = binData.provider
                        result_bin_data_card_type.text = binData.cardType
                        result_bin_data_foreign_card.text = binData.foreignCard.toString()
                        result_bin_data_code.text = binData.code
                        result_bin_data_message.text = binData.message
                        result_bin_data_corporate_card.text = binData.corporateCard.toString()
                        result_bin_data_issuer.text = binData.issuer
                        result_bin_data_issuer_code.text = binData.issuerCode
                        result_bin_data_card_bin.text = binData.cardBin
                        result_bin_data_last_four.text = binData.lastFourDigits

                        result_content.visibility = View.VISIBLE
                        backToForm.visibility = View.VISIBLE
                    }
                } else {
                    result_content.visibility = View.GONE
                }

                loading.visibility = View.INVISIBLE
            }
        }

        backToForm.setOnClickListener {
            holder.text?.clear()
            card_number.text?.clear()
            card_expiration_month.text.clear()
            card_expiration_year.text.clear()
            card_security_code.text?.clear()
            card_brand.text.clear()
            result_content.visibility = View.INVISIBLE
            errors_list_view.visibility = View.INVISIBLE
            backToForm.visibility = View.INVISIBLE
            form.visibility = View.VISIBLE
        }
    }
}