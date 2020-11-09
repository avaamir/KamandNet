package mp.amir.ir.kamandnet.ui.activities

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import kotlinx.android.synthetic.main.item_pic_placeholder.view.*
import mp.amir.ir.kamandnet.R
import mp.amir.ir.kamandnet.databinding.ActivityInstructionBinding
import mp.amir.ir.kamandnet.models.Instruction
import mp.amir.ir.kamandnet.ui.dialogs.QRorNFCDialog
import mp.amir.ir.kamandnet.utils.general.alert
import mp.amir.ir.kamandnet.utils.general.toast
import mp.amir.ir.kamandnet.utils.wewi.Constants

class InstructionActivity : AppCompatActivity(), QRorNFCDialog.Interactions {


    companion object {
        private const val CAPTURE_IMAGE_REQ = 12
    }

    private var turn = 0

    private lateinit var mBinding: ActivityInstructionBinding
    private lateinit var instruction: Instruction

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_instruction)


        instruction = intent.getParcelableExtra(Constants.INTENT_INSTRUCTION_ACTIVITY_DATA)!!

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_instruction)
        mBinding.instruction = instruction

        initViews()

    }

    private fun initViews() {
        mBinding.ivBack.setOnClickListener {
            //TODO if has change show dialog
            alert(
                "توجه",
                "تغییرات ذخیره شود؟",
                "بله",
                "خیر",
                false,
                {
                    finish()
                },
                {
                    //TODO save pics to memory and has address in db
                    //TODO save to DB
                    finish()
                })
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

        mBinding.btnSave.setOnClickListener {
            QRorNFCDialog(this, R.style.my_alert_dialog, this).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAPTURE_IMAGE_REQ) {
            if (resultCode == RESULT_OK) {
                val bitmap = data?.extras?.get("data") as Bitmap?
                if (bitmap != null) {
                    onChoseImage(bitmap)
                } else {
                    toast("خطایی به وجود آمد")
                }
            }
        }


    }


    private fun onChoseImage(bitmap: Bitmap) {
        turn++
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
        chosenFrame.framePlaceHolder.visibility = View.GONE
        chosenFrame.framePic.visibility = View.VISIBLE
        chosenFrame.framePic.ivPic.setImageBitmap(bitmap)
    }

    override fun onQRClicked() {
        toast("Not yet implemented QR")
    }

    override fun onNFCClicked() {
        toast("Not yet implemented NFC")
    }


}