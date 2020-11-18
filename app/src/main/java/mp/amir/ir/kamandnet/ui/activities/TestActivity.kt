package mp.amir.ir.kamandnet.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_test.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import mp.amir.ir.kamandnet.R
import mp.amir.ir.kamandnet.respository.apiservice.ApiService

class TestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        initViews()
    }

    private fun initViews() {
        btnChangeDomain.setOnClickListener {
            
        }
        btnReqApi.setOnClickListener {
            CoroutineScope(IO).launch {
                ApiService.client.getInstructions()
            }
        }
    }
}