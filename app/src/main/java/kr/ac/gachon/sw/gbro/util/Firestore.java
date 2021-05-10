package kr.ac.gachon.sw.gbro.util;

import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;

import kr.ac.gachon.sw.gbro.util.model.Post;
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
     * @param userId Firebase UID
     * @param userEmail 유저 이메일
     * @param userNickName 유저 닉네임
     * @return Task<Void>
     */
    public static Task<Void> writeNewUser(String userId, String userEmail, String userNickName) {
        User newUser = new User(userEmail, userNickName, null, new Timestamp(new Date()));
        return getFirestoreInstance().collection("user").document(userId).set(newUser);
    }
    
    /**
     *
     * @param type
     * @param title
     * @param content
     * @param photoUrls
     * @param summaryBuildingType
     * @param locationList
     * @param writerId
     * @param writeTime
     * @param isFinished
     * @return
     */
    public static Task<Void> writeNewPost(int type, String title, String content, ArrayList<String> photoUrls, int summaryBuildingType, ArrayList<GeoPoint> locationList, String writerId, Timestamp writeTime, boolean isFinished) {
        Post newPost = new Post(type, title, content, photoUrls, summaryBuildingType, locationList, writerId, writeTime, isFinished);
        return getFirestoreInstance().collection("post").document().set(newPost);
    }

    /*
     * 유저의 정보를 DB에서 제거하도록 요청한다
     * @author Minjae Seon
     * @param userId Firebase UID
     * @return Task<Void>
     */
    public static Task<Void> removeUser(String userId) {
        return getFirestoreInstance().collection("user").document(userId).delete();
    }

    /*
     * 게시물을 DB에서 삭제하도록 요청한다
     * @author Taehyun Park
     * @param postId
     * @return Task<Void>
     */
    public static Task<Void> removePost(String postId) {
        return getFirestoreInstance().collection("post").document(postId).delete();
    }

    /*
     * 유저 정보를 가져온다
     * @author Minjae Seon
     * @param userId Firebase UID
     * @return Task<DocumentSnapshot>
     */
    public static Task<DocumentSnapshot> getUserData(String userId) {
        return getFirestoreInstance().collection("user").document(userId).get();
    }

    /*
     * 게시물을 가져온다
     * @author Taehyun Park
     * @param
     * @param
     * @param
     * @return Task<DocumentSnapshot>
     */
    public static Task<QuerySnapshot> getPostData(int type) {
        if(type == 0)
            return getFirestoreInstance().collection("post").orderBy("writeTime",Query.Direction.DESCENDING).limit(20).get();
        else
            return getFirestoreInstance().collection("post").whereEqualTo("type",type).orderBy("writeTime",Query.Direction.ASCENDING).limit(20).get();
    }
}
