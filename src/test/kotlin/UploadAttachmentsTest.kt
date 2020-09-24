import kotlin.test.*
import assert4k.*
import java.io.File

class UploadAttachmentsTest {

    @Test
    fun `can upload attachments`() {

        // Given
        val upload = UploadAttachments()
        val attachments = listOf("one", "two", "three")

        // When
        val result = upload(attachments)

        // Then
        assert that result equals listOf(
            UploadAttachments.Result.Success,
            UploadAttachments.Result.Success,
            UploadAttachments.Result.Success,
        )
    }

    @Test
    fun `returns proper network error`() {

        // Given
        val upload = UploadAttachments(
            upload = {
                if (it.name == "one") throw Exception("File too big!")
                else { Unit /* upload regularly */ }
            }
        )
        val attachments = listOf("one", "two", "three")

        // When
        val result = upload(attachments)

        // Then
        assert that result equals listOf(
            UploadAttachments.Result.Error.Network,
            UploadAttachments.Result.Success,
            UploadAttachments.Result.Success,
        )
    }

    @Test
    fun `returns proper file error`() {

        // Given
        val upload = UploadAttachments(
            getAttachment = {
                if (it == "two") throw NoSuchFileException(File(it))
                else getAttachmentById(it)
            }
        )
        val attachments = listOf("one", "two", "three")

        // When
        val result = upload(attachments)

        // Then
        assert that result equals listOf(
            UploadAttachments.Result.Success,
            UploadAttachments.Result.Error.FilNotFound,
            UploadAttachments.Result.Success,
        )
    }
}
