package com.example.productstopreapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class StoreActivity : AppCompatActivity() {

    private lateinit var productName: EditText
    private lateinit var productPrice: EditText
    private lateinit var productDescription: EditText
    private lateinit var productImage: ImageView
    private lateinit var addProductButton: Button
    private lateinit var productList: RecyclerView
    private lateinit var exitButton: Button

    private val products = mutableListOf<Product>()
    private lateinit var productAdapter: ProductAdapter

    private var selectedImageUri: Uri? = null


    private val updateProductActivityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val updatedProduct = result.data?.getParcelableExtra<Product>("updatedProduct")
            updatedProduct?.let {

                val index = products.indexOfFirst { it.name == updatedProduct.name }
                if (index != -1) {
                    products[index] = updatedProduct
                    productAdapter.notifyItemChanged(index)
                }
            }
        }
    }


    private val pickImageRequestCode = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_store)


        productName = findViewById(R.id.productName)
        productPrice = findViewById(R.id.productPrice)
        productDescription = findViewById(R.id.productDescription)
        productImage = findViewById(R.id.productImage)
        addProductButton = findViewById(R.id.addProductButton)
        productList = findViewById(R.id.productList)
        exitButton = findViewById(R.id.exitButton)


        productList.layoutManager = LinearLayoutManager(this)
        productAdapter = ProductAdapter(products) { product ->
            val intent = Intent(this, ProductDetailActivity::class.java).apply {
                putExtra("product", product)
            }
            updateProductActivityResultLauncher.launch(intent)
        }
        productList.adapter = productAdapter


        val selectImageButton: Button = findViewById(R.id.selectImageButton)
        selectImageButton.setOnClickListener {

            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, pickImageRequestCode)
        }


        addProductButton.setOnClickListener {
            val name = productName.text.toString()
            val price = productPrice.text.toString().toDoubleOrNull() ?: 0.0
            val description = productDescription.text.toString()

            if (name.isNotBlank() && price > 0 && description.isNotBlank()) {
                val product = Product(name, price, description, selectedImageUri?.toString())
                products.add(product)
                productAdapter.notifyItemInserted(products.size - 1)


                productName.text.clear()
                productPrice.text.clear()
                productDescription.text.clear()
                productImage.setImageResource(R.drawable.ic_launcher_foreground)
                selectedImageUri = null
            } else {
                Toast.makeText(this, "Пожалуйста, заполните все поля", Toast.LENGTH_SHORT).show()
            }
        }


        exitButton.setOnClickListener {

            Toast.makeText(this, "Программа завершена", Toast.LENGTH_SHORT).show()
            finish()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == pickImageRequestCode && resultCode == RESULT_OK && data != null) {
            val selectedImageUri: Uri? = data.data
            selectedImageUri?.let {

                productImage.setImageURI(it)
                this.selectedImageUri = it
            } ?: run {

                Toast.makeText(this, "Не удалось выбрать изображение", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}
