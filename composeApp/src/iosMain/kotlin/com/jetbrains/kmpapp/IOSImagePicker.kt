// File: composeApp/src/iosMain/kotlin/com/jetbrains/kmpapp/IOSImagePicker.kt
package com.jetbrains.kmpapp

import com.jetbrains.kmpapp.screens.images.ImagePicker
import kotlinx.cinterop.*
import platform.Foundation.*
import platform.UIKit.*
import platform.AVFoundation.*
import platform.Photos.*

@OptIn(ExperimentalForeignApi::class)
class IOSImagePicker(private val viewController: UIViewController) : ImagePicker {

    private var onResult: ((String?) -> Unit)? = null

    override fun pickFromGallery(onResult: (String?) -> Unit) {
        this.onResult = onResult

        // ตรวจสอบ permission สำหรับ Photo Library
        checkPhotoLibraryPermission { granted ->
            if (granted) {
                presentImagePicker(sourceType = UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypePhotoLibrary)
            } else {
                onResult(null)
            }
        }
    }

    override fun takePhoto(onResult: (String?) -> Unit) {
        this.onResult = onResult

        // ตรวจสอบ permission สำหรับกล้อง
        checkCameraPermission { granted ->
            if (granted) {
                presentImagePicker(sourceType = UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypeCamera)
            } else {
                onResult(null)
            }
        }
    }

    private fun checkPhotoLibraryPermission(callback: (Boolean) -> Unit) {
        val status = PHPhotoLibrary.authorizationStatus()

        when (status) {
            PHAuthorizationStatus.PHAuthorizationStatusAuthorized,
            PHAuthorizationStatus.PHAuthorizationStatusLimited -> {
                callback(true)
            }
            PHAuthorizationStatus.PHAuthorizationStatusNotDetermined -> {
                PHPhotoLibrary.requestAuthorization { newStatus ->
                    callback(
                        newStatus == PHAuthorizationStatus.PHAuthorizationStatusAuthorized ||
                                newStatus == PHAuthorizationStatus.PHAuthorizationStatusLimited
                    )
                }
            }
            else -> {
                callback(false)
            }
        }
    }

    private fun checkCameraPermission(callback: (Boolean) -> Unit) {
        val status = AVCaptureDevice.authorizationStatusForMediaType(AVMediaTypeVideo)

        when (status) {
            AVAuthorizationStatus.AVAuthorizationStatusAuthorized -> {
                callback(true)
            }
            AVAuthorizationStatus.AVAuthorizationStatusNotDetermined -> {
                AVCaptureDevice.requestAccessForMediaType(AVMediaTypeVideo) { granted ->
                    callback(granted)
                }
            }
            else -> {
                callback(false)
            }
        }
    }

    private fun presentImagePicker(sourceType: UIImagePickerControllerSourceType) {
        // ตรวจสอบว่า source type นี้ใช้ได้หรือไม่
        if (!UIImagePickerController.isSourceTypeAvailable(sourceType)) {
            onResult?.invoke(null)
            return
        }

        val imagePicker = UIImagePickerController().apply {
            setSourceType(sourceType)
            setAllowsEditing(false)
            setDelegate(ImagePickerDelegate(onImageSelected = { imageUrl ->
                onResult?.invoke(imageUrl)
                onResult = null
            }))
        }

        viewController.presentViewController(imagePicker, animated = true, completion = null)
    }

    private fun saveImageToDocuments(image: UIImage): String? {
        val documentsPath = NSSearchPathForDirectoriesInDomains(
            NSDocumentDirectory,
            NSUserDomainMask,
            true
        ).firstOrNull() as? String ?: return null

        val timestamp = NSDate().timeIntervalSince1970.toLong()
        val fileName = "image_$timestamp.jpg"
        val filePath = "$documentsPath/$fileName"

        val imageData = UIImageJPEGRepresentation(image, 0.8)
        return if (imageData?.writeToFile(filePath, atomically = true) == true) {
            "file://$filePath"
        } else {
            null
        }
    }
}

@OptIn(ExperimentalForeignApi::class)
private class ImagePickerDelegate(
    private val onImageSelected: (String?) -> Unit
) : NSObject(), UIImagePickerControllerDelegateProtocol, UINavigationControllerDelegateProtocol {

    override fun imagePickerController(
        picker: UIImagePickerController,
        didFinishPickingMediaWithInfo: Map<Any?, *>
    ) {
        val image = didFinishPickingMediaWithInfo[UIImagePickerControllerOriginalImage] as? UIImage

        if (image != null) {
            val imageUrl = saveImageToDocuments(image)
            onImageSelected(imageUrl)
        } else {
            onImageSelected(null)
        }

        picker.dismissViewControllerAnimated(true, completion = null)
    }

    override fun imagePickerControllerDidCancel(picker: UIImagePickerController) {
        onImageSelected(null)
        picker.dismissViewControllerAnimated(true, completion = null)
    }

    private fun saveImageToDocuments(image: UIImage): String? {
        val documentsPath = NSSearchPathForDirectoriesInDomains(
            NSDocumentDirectory,
            NSUserDomainMask,
            true
        ).firstOrNull() as? String ?: return null

        val timestamp = NSDate().timeIntervalSince1970.toLong()
        val fileName = "image_$timestamp.jpg"
        val filePath = "$documentsPath/$fileName"

        val imageData = UIImageJPEGRepresentation(image, 0.8)
        return if (imageData?.writeToFile(filePath, atomically = true) == true) {
            "file://$filePath"
        } else {
            null
        }
    }
}