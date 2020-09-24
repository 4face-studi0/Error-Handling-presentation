import UploadAttachments.Result.Error
import UploadAttachments.Result.Success
import kotlin.random.Random

class UploadAttachments(
    private val getAttachment: (id: String) -> Attachment = ::getAttachmentById,
    private val upload: (Attachment) -> Unit = ::upload
) {

    operator fun invoke(attachmentsIds: Collection<String>): Collection<Result> =
        attachmentsIds.map { id ->

            val attachment = runCatching { getAttachment(id) }
                .onFailure { return@map Error.FilNotFound }
                .getOrThrow()

            runCatching { upload(attachment) }
                .fold(
                    onSuccess = { Success },
                    onFailure = { Error.Network }
                )
        }

    sealed class Result {
        object Success : Result()
        sealed class Error : Result() {
            object Network : Error()
            object FilNotFound : Error()
        }
    }
}

// Deep in the business login
/**
 * @return [Attachment]
 * @throws NoSuchFileException
 */
fun getAttachmentById(id: String): Attachment {
    // ... search from local source
    return Attachment(id, Random.nextInt())
}

/**
 * Upload attachment to server
 * @throws Exception
 */
private fun upload(@Suppress("UNUSED_PARAMETER") attachment: Attachment) {
    // ... upload to server
}

data class Attachment(
    val name: String,
    val body: Int
)
