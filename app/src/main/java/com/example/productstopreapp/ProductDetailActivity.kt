package com.example.productstopreapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

class ProductDetailActivity : AppCompatActivity() {

    private lateinit var productName: EditText
    private lateinit var productPrice: EditText
    private lateinit var productDescription: EditText
    private lateinit var productImage: ImageView
    private lateinit var saveButton: Button

    private var product: Product? = null
    private var selectedImageUri: Uri? = null


    private val getImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            selectedImageUri = it
            productImage.setImageURI(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_detail)


        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        productName = findViewById(R.id.productName)
        productPrice = findViewById(R.id.productPrice)
        productDescription = findViewById(R.id.productDescription)
        productImage = findViewById(R.id.productImage)
        saveButton = findViewById(R.id.saveButton)


        product = intent.getParcelableExtra("product")

        product?.let {
            productName.setText(it.name)
            productPrice.setText(it.price.toString())
            productDescription.setText(it.description)
            it.imageUri?.let { uri ->
                selectedImageUri = Uri.parse(uri)
                productImage.setImageURI(Uri.parse(uri))
            }
        }


        productImage.setOnClickListener {

            getImage.launch("image/*")
        }


        saveButton.setOnClickListener {
            val updatedName = productName.text.toString()
            val updatedPrice = productPrice.text.toString().toDoubleOrNull() ?: 0.0
            val updatedDescription = productDescription.text.toString()


            if (updatedName.isNotBlank() && updatedPrice > 0 && updatedDescription.isNotBlank()) {
                val updatedProduct = Product(
                    updatedName,
                    updatedPrice,
                    updatedDescription,
                    selectedImageUri?.toString()
                )


                val resultIntent = Intent().apply {
                    putExtra("updatedProduct", updatedProduct)
                }
                setResult(RESULT_OK, resultIntent)
                finish()
            } else {
                Toast.makeText(this, "Пожалуйста, заполните все поля корректно", Toast.LENGTH_SHORT).show()
            }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_product_detail, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.back -> {

                onBackPressed()
                true
            }
            R.id.action_exit -> {

                finishAffinity()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
