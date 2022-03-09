package ru.usatu.pass

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import ru.usatu.pass.databinding.ActivityMainBinding
import ru.usatu.pass.databinding.FragmentMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: FragmentMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = FragmentMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.settings.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }
    }
}