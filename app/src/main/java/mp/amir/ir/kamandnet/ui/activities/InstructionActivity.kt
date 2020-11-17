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
import mp.amir.ir.kamandnet.models.enums.RepairType
import mp.amir.ir.kamandnet.models.enums.TagType.*
import mp.amir.ir.kamandnet.respository.apiservice.ApiService
import mp.amir.ir.kamandnet.ui.dialogs.NoNetworkDialog
import mp.amir.ir.kamandnet.utils.general.*
import mp.amir.ir.kamandnet.utils.kamand.Constants
import mp.amir.ir.kamandnet.viewmodels.InstructionActivityViewModel

class InstructionActivity : AppCompatActivity(), ApiService.InternetConnectionListener, ApiService.OnUnauthorizedListener {


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

        val instruction = viewModel.instruction!!

        //TODO mishe az bindingAdapter estefade kard chun tuye instructionAdapter ham daghighan hamin code tekrar shode
        mBinding.layoutInstruction.ivRepairType.setImageResource(
            when (instruction.repairType) {
                RepairType.PDM -> R.drawable.ic_pm
                RepairType.EM -> R.drawable.ic_run
                RepairType.CM -> R.drawable.ic_cm
                RepairType.PM -> R.drawable.ic_pdm
            }.exhaustiveAsExpression()
        )

        instruction.submitFlowModel?.let {
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


        if (instruction.submitFlowModel?.scannedTagCode != null) {
            mBinding.btnScan.visibility = View.GONE
            mBinding.btnSave.visibility = View.VISIBLE
        } else when (instruction.tagType) {
            None -> {
                mBinding.btnSave.visibility = View.VISIBLE
                mBinding.btnScan.visibility = View.GONE
            }
            QR -> mBinding.btnScan.setOnClickListener {
                startActivityForResult(
                    Intent(this, QRScannerActivity::class.java).apply {
                        putParcelableExtra(
                            Constants.INTENT_INSTRUCTION_DATA,
                            instruction
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
                            instruction
                        )
                    },
                    NFC_SCANNER_REQ
                )
            }
        }.exhaustive()


        mBinding.btnSave.setOnClickListener {
            val description = mBinding.etDesc.text.toString().trim()
            if (description.isNotEmpty()) {
                viewModel.submitResult(description = description)
                viewModel.submitToServer()
                mBinding.btnSave.showProgressBar(true)
                finish()
            } else {
                toast("حداقل وارد کردن متن توضیحات الزامی میباشد")
            }
        }
    }

    private fun subscribeObservers() {
        viewModel.submitInstructionResponse.observe(this, {
            if (it != null) {
                if (it.isSucceed) {
                    toast(it.message)
                } else {
                    toast(it.message)
                }
            } else {
                toast("خطا در ارسال اطلاعات به سرور. لطفا وضعیت شبکه خود را بررسی کنید")
            }
        })
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
                        viewModel.saveImage(this, bitmap, turn)
                        onChoseImage(bitmap)
                    } else {
                        toast("خطایی به وجود آمد")
                    }
                }
            }
            QR_SCANNER_REQ, NFC_SCANNER_REQ -> {
                if (resultCode == RESULT_OK) {
                    val scannedCode = data?.extras?.get(Constants.INTENT_SCAN_TAG_RESULT_TEXT) as String
                    viewModel.submitResult(scannedTagCode = scannedCode)
                    toast("scannedCode:$scannedCode -> for test purpose")
                    mBinding.btnScan.visibility = View.GONE
                    mBinding.btnSave.visibility = View.VISIBLE
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


    override fun onUnauthorizedAction(event: Event<Unit>) {
            finish()
    }

    override fun onInternetUnavailable() {
        NoNetworkDialog(this, R.style.my_alert_dialog).show()
    }
}