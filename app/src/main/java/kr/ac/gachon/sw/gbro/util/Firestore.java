package kr.ac.gachon.sw.gbro.util;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import kr.ac.gachon.sw.gbro.util.model.User;

/*
 * Firebase Firestore 관련 함수 Class
 * @author Minjae Seon
 */
public class Firestore {
    /*
     * Firestore의 Instance를 반환한다
     * @author Minjae Seon
     * @return FirebaseFirestore Instance
     */
    public static FirebaseFirestore getFirestoreInstance() {
        return FirebaseFirestore.getInstance();
    }

    /*
     * 새로운 유저의 정보를 DB에 추가하도록 요청한다
     * @author Minjae Seon
     * @param userId 유저 아이디 (이메일)
     * @param userNickName 유저 닉네임
     * @return Task<Void>
     */
    public static Task<Void> writeNewUser(String userId, String userNickName) {
        User newUser = new User(userId, userNickName, null);
        return getFirestoreInstance().collection("user").document(userId).set(newUser);
    }

    /*
     * 유저의 정보를 DB에서 제거하도록 요청한다
     * @author Minjae Seon
     * @param userId 유저 아이디 (이메일)
     * @return Task<Void>
     */
    public static Task<Void> removeUser(String userId) {
        return getFirestoreInstance().collection("user").document(userId).delete();
    }
}
