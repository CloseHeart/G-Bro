package kr.ac.gachon.sw.gbro.util;

import android.graphics.Bitmap;

import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

public class CloudStorage {
    /**
     * Profile 이미지 저장 폴더 Reference
     * Profile 이미지는 user/[User Id] 내에 저장되어야 함
     */
    public static StorageReference profileRef = getStorageInstance().getReference().child("user");

    /**
     * 게시글 이미지 저장 폴더 Reference
     * 게시글은 post/랜덤 ID 형태로 저장되어야 함
     */
    public static StorageReference postRef = getStorageInstance().getReference().child("post");

    /**
     * Storage Instance를 가져온다
     * @author Minjae Seon
     * @return FirebaseStorage Instance
     */
    public static FirebaseStorage getStorageInstance() {
        return FirebaseStorage.getInstance();
    }

    /**
     * 사용자 Profile Image를 업로드한다
     * @author Minjae Seon
     * @param userId 사용자 Firebase ID
     * @param bitmap Image Bitmap
     * @return Task<UploadTask.TaskSnapshot> (업로드 Task)
     */
    public static Task<UploadTask.TaskSnapshot> uploadProfileImg(String userId, Bitmap bitmap) {
        StorageReference userProfileRef = profileRef.child(userId + "/profile.jpg");
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] data = byteArrayOutputStream.toByteArray();

        return userProfileRef.putBytes(data);
    }

    /**
     * 게시글 Image를 업로드 한다
     * @param bitmap 업로드할 Bitmap
     * @return Task<UploadTask.TaskSnapshot> (업로드 Task)
     */
    public static Task<UploadTask.TaskSnapshot> uploadPostImg(Bitmap bitmap) {
        StorageReference postFile = postRef.child(UUID.randomUUID().toString() + ".jpg");
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] data = byteArrayOutputStream.toByteArray();

        return postFile.putBytes(data);
    }

    /**
     * URL을 통해 이미지를 불러온다
     * @param URL 이미지 URL
     * @return Task<byte[]></byte[]>
     */
    public static Task<byte[]> getImageFromURL(String URL) {
        StorageReference profileReference = getStorageInstance().getReferenceFromUrl(URL);
        return profileReference.getBytes(1500000);
    }
}
