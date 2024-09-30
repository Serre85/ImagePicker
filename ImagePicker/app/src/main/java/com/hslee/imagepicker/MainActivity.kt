package com.hslee.imagepicker

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.loader.content.CursorLoader
import com.hslee.imagepicker.databinding.ActivityMainBinding
import java.util.concurrent.Executor

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding


    private lateinit var pickMultipleMediaLauncher: ActivityResultLauncher<Intent>

    private lateinit var pickSingleMediaLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.btnPicker.setOnClickListener {
            pickSingleMediaLauncher.launch(
                Intent(MediaStore.ACTION_PICK_IMAGES).apply {
                    type = "image/*"
                })
        }

        binding.btnMultiPicker.setOnClickListener {
            pickMultipleMediaLauncher.launch(
                Intent(MediaStore.ACTION_PICK_IMAGES).apply {
                    type = "image/*"
                    putExtra(MediaStore.EXTRA_PICK_IMAGES_MAX, 3) // 최대 3장까지
                })
        }

        pickMultipleMediaLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == RESULT_OK) {
                    val uris = it.data?.clipData ?: return@registerForActivityResult

                    for (i in 0 until uris.itemCount) {
                        val path = getRealPathFromURI(uris.getItemAt(i).uri)
                        Log.i("1234", "1234 $path")
                    }
                }
            }

        pickSingleMediaLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == RESULT_OK) {
                    it.data?.data?.let { uri ->
                        val path = getRealPathFromURI(uri)
                        Log.i("1234", "1234 $path")
                    }
                }
            }
    }

    private fun getRealPathFromURI(contentUri: Uri): String? {
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val loader = CursorLoader(this@MainActivity, contentUri, proj, null, null, null)
        val cursor = loader.loadInBackground()!!
        val index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()
        val result = cursor.getString(index)
        cursor.close()
        return result
    }
}