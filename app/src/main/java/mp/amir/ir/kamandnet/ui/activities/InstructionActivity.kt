package mp.amir.ir.kamandnet.ui.activities

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.item_pic_placeholder.view.*
import mp.amir.ir.kamandnet.R
import mp.amir.ir.kamandnet.databinding.ActivityInstructionBinding
import mp.amir.ir.kamandnet.models.api.TagType.*
import mp.amir.ir.kamandnet.utils.general.exhaustive
import mp.amir.ir.kamandnet.utils.general.putParcelableExtra
import mp.amir.ir.kamandnet.utils.general.toast
import mp.amir.ir.kamandnet.utils.kamand.Constants
import mp.amir.ir.kamandnet.viewmodels.InstructionActivityViewModel

class InstructionActivity : AppCompatActivity() {


    companion object {
        private const val CAPTURE_IMAGE_REQ = 12
        private const val QR_SCANNER_REQ = 13
        private const val NFC_SCANNER_REQ = 14
    }

    private var turn = 1

    private lateinit var viewModel: InstructionActivityViewModel
    private lateinit var mBinding: ActivityInstructionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_instruction)

        viewModel = ViewModelProvider(this).get(InstructionActivityViewModel::class.java)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_instruction)

        if (viewModel.instruction == null)
            viewModel.instruction =
                intent.getParcelableExtra(Constants.INTENT_INSTRUCTION_DATA)!!


        mBinding.instruction = viewModel.instruction

        initViews()
        subscribeObservers()
    }


    private fun initViews() {

        viewModel.instruction!!.submitFlowModel?.let {
            it.images.forEach { file ->
                if (!file.exists()) {
                    throw Exception("file does not exist: ${file.absolutePath}") //TODO mishe age vojud nadsht az to db addresesh ro hazf kard, soal ine ke chera vojud nadashte bashe??
                }
                val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                if (turn > 5)
                    throw Exception("turn is more than 5")
                onChoseImage(bitmap)
            }
        }

        mBinding.ivBack.setOnClickListener {
            onBackPressed()
        }

        mBinding.frameAddPic.setOnClickListener {
            if (turn > 5) {
                toast("تنها میتوانید 5 عکس بارگذاری کنید", false)
                return@setOnClickListener
            }

            startActivityForResult(
                Intent(MediaStore.ACTION_IMAGE_CAPTURE),
                CAPTURE_IMAGE_REQ
            )
        }


        if (viewModel.instruction!!.submitFlowModel?.scannedTagCode != null) {
            mBinding.btnScan.visibility = View.GONE
        } else when (viewModel.instruction!!.tagType) {
            None -> mBinding.btnScan.visibility = View.GONE
            QR -> mBinding.btnScan.setOnClickListener {
                startActivityForResult(
                    Intent(this, QRScannerActivity::class.java).apply {
                        putParcelableExtra(
                            Constants.INTENT_INSTRUCTION_DATA,
                            viewModel.instruction!!
                        )
                    },
                    QR_SCANNER_REQ
                )
            }
            NFC -> mBinding.btnScan.setOnClickListener {
                startActivityForResult(
                    Intent(this, NfcActivity::class.java).apply {
                        putParcelableExtra(
                            Constants.INTENT_INSTRUCTION_DATA,
                            viewModel.instruction!!
                        )
                    },
                    NFC_SCANNER_REQ
                )
            }
        }.exhaustive()


        mBinding.btnSave.setOnClickListener {
            val description = mBinding.etDesc.text.toString().trim()
            viewModel.submitResult(description = description)
            finish()
        }
    }

    private fun subscribeObservers() {
        /*viewModel.submitInstructionResponse.observe(this, {
            mBinding.btnSave.showProgressBar(false)
            if (it != null) {
                if (it.isSucceed) {
                    //TODO should update LocalRepo and delete this flow from LOCAL DB then finish activity
                } else {
                    //TODO
                }
            } else {

            }
        })*/
    }

    override fun onBackPressed() {
        //TODO if has change show dialog
        /* alert(
             "توجه",
             "تغییرات ذخیره شود؟",
             "بله",
             "خیر",
             true,
             {
                 super.onBackPressed()
             },
             {
                 //TODO save pics to memory and has address in db
                 //TODO save to DB
                 super.onBackPressed()
             })*/
        val description = mBinding.etDesc.text.toString().trim()
        viewModel.submitResult(description = description)
        super.onBackPressed()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            CAPTURE_IMAGE_REQ -> {
                if (resultCode == RESULT_OK) {
                    val bitmap = data?.extras?.get("data") as Bitmap?
                    if (bitmap != null) {
                        onChoseImage(bitmap)
                        viewModel.saveImage(this, bitmap, turn)
                    } else {
                        toast("خطایی به وجود آمد")
                    }
                }
            }
            QR_SCANNER_REQ -> {
                if (resultCode == RESULT_OK) {
                    val qrCode = data?.extras?.get(Constants.INTENT_SCAN_TAG_RESULT_TEXT) as String
                    viewModel.submitResult(scannedTagCode = qrCode)
                    mBinding.btnScan.visibility = View.GONE
                }
            }
            NFC_SCANNER_REQ -> {
                if (resultCode == RESULT_OK) {
                    val nfcCode = data?.extras?.get(Constants.INTENT_SCAN_TAG_RESULT_TEXT) as String
                    //TODO check the qr code with server code
                    viewModel.submitResult(scannedTagCode = nfcCode)
                    toast(nfcCode)
                    mBinding.btnScan.visibility = View.GONE
                }
            }
        }


    }


    private fun onChoseImage(bitmap: Bitmap) {
        println("debug: $turn")
        val chosenFrame = when (turn) {
            1 -> mBinding.framePic1
            2 -> mBinding.framePic2
            3 -> mBinding.framePic3
            4 -> mBinding.framePic4
            5 -> mBinding.framePic5
            else -> null
        }
        if (chosenFrame == null) {
            toast("تنها میتوانید 5 عکس بارگذاری کنید", false)
            return
        }
        turn++
        chosenFrame.framePlaceHolder.visibility = View.GONE
        chosenFrame.framePic.visibility = View.VISIBLE
        chosenFrame.framePic.ivPic.setImageBitmap(bitmap)
    }


}