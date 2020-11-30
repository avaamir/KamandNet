package mp.amir.ir.kamandnet.utils.kamand

object Constants {
    const val INTENT_SETTINGS_DOMAIN_NOT_INIT = "SETTI-DO-N-INI"
    const val INTENT_SCAN_TAG_RESULT_TEXT = "qr-nfc-result"
    const val INTENT_UPDATE_ACTIVITY_UPDATE_RESPONSE = "update-ac"
    const val INTENT_INSTRUCTION_DATA = "inst-act-data"

    val LARGE_TEST_TEXT by lazy { "لورم ایپسوم یا طرح\u200Cنما (به انگلیسی: Lorem ipsum) به متنی آزمایشی و بی\u200Cمعنی در صنعت چاپ، صفحه\u200Cآرایی و طراحی گرافیک گفته می\u200Cشود. طراح گرافیک از این متن به عنوان عنصری از ترکیب بندی برای پر کردن صفحه و ارایه اولیه شکل ظاهری و کلی طرح سفارش گرفته شده استفاده می نماید، تا از نظر گرافیکی نشانگر چگونگی نوع و اندازه فونت و ظاهر متن باشد. معمولا طراحان گرافیک برای صفحه\u200Cآرایی، نخست از متن\u200Cهای آزمایشی و بی\u200Cمعنی استفاده می\u200Cکنند تا صرفا به مشتری یا صاحب کار خود نشان دهند که صفحه طراحی یا صفحه بندی شده بعد از اینکه متن در آن قرار گیرد چگونه به نظر می\u200Cرسد و قلم\u200Cها و اندازه\u200Cبندی\u200Cها چگونه در نظر گرفته شده\u200Cاست. از آنجایی که طراحان عموما نویسنده متن نیستند و وظیفه رعایت حق تکثیر متون را ندارند و در همان حال کار آنها به نوعی وابسته به متن می\u200Cباشد آنها با استفاده از محتویات ساختگی، صفحه گرافیکی خود را صفحه\u200Cآرایی می\u200Cکنند تا مرحله طراحی و صفحه\u200Cبندی را به پایان برند." }

    val SERVER_ERROR by lazy { "خطا در برقراری ارتباط با سرور" }

    val UNAUTHORIZED_MSG by lazy { "حساب کاربری شما در دستگاه دیگری فعال است. لطفا دوباره وارد شوید" }

}