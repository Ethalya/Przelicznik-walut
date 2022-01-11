package com.example.currencyconversionapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.lang.Exception
import java.net.URL

class MainActivity : AppCompatActivity() {

    var baseCurrency = "EUR"
    var convertedToCurrency = "PLN"
    var conversionRate = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        spinnerSetup()
        textChange()
    }

    private fun textChange() {
        val etfirstConversion = findViewById<EditText>(R.id.et_firstConversion)

        etfirstConversion.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                try {
                    getApiResult()
                } catch (e: Exception) {
                    Log.e("Main", "$e")
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                Log.d("Main", "Before Text Changed")
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                Log.d("Main", "On Text Changed")
            }
        })
    }

    //ustawianie walut w spinnerach, pobieranie z API aktualnych wartości
    private fun spinnerSetup() {
        val spinner: Spinner = findViewById(R.id.spinner_firstConversion)
        val spinner2: Spinner = findViewById(R.id.spinner_secondConversion)

        ArrayAdapter.createFromResource(
            this,
            R.array.waluty,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }

        ArrayAdapter.createFromResource(
            this,
            R.array.waluty2,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner2.adapter = adapter
        }

        spinner.onItemSelectedListener = (object: AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                baseCurrency = parent?.getItemAtPosition(position).toString()
                getApiResult()
            }
        })

        spinner2.onItemSelectedListener = (object: AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                convertedToCurrency = parent?.getItemAtPosition(position).toString()
                getApiResult()
            }
        })
    }

    //pobieranie z API
    private fun getApiResult(){
        val etfirstConversion = findViewById<EditText>(R.id.et_firstConversion)
        val etsecondConversion = findViewById<EditText>(R.id.et_secondConversion)

        if(etfirstConversion != null && etfirstConversion.text.isNotEmpty() && etfirstConversion.text.isNotBlank()){
            //link do API
            val API = "http://api.exchangeratesapi.io/v1/latest?access_key=df9ccab43c59a1c0087892ae3269e02d"

            //powiadomienie toast o błędzie
            if(baseCurrency == convertedToCurrency) {
                Toast.makeText(applicationContext, "Nie można przekonwertować takich samych walut", Toast.LENGTH_SHORT).show()
            }
            else {
                GlobalScope.launch(Dispatchers.IO) {
                    try {
                        val apiResult = URL(API).readText()
                        val jsonObject = JSONObject(apiResult)

                        conversionRate = jsonObject.getJSONObject("rates").getString(convertedToCurrency).toFloat()

                        Log.d("Main", "$conversionRate")
                        Log.d("Main", apiResult)

                        withContext(Dispatchers.Main) {
                            val text = ((etfirstConversion.text.toString().toFloat()) * conversionRate).toString()
                            etsecondConversion?.setText(text)
                        }

                    } catch (e: Exception) {
                        Log.e("Main", "$e")
                    }
                }
            }
        }
    }
}