package kr.ac.gachon.sw.gbro.util;

import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import kr.ac.gachon.sw.gbro.util.model.ChatData;
import kr.ac.gachon.sw.gbro.util.model.ChatRoom;
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
    
    /*
     * 새로운 게시물의 정보를 DB에 추가하도록 요청한다.
     * @author Taehyun Park
     * @param type
     * @param title
     * @param content
     * @param summaryBuildingType
     * @param locationList
     * @param writerId
     * @param writeTime
     * @param isFinished
     * @return Task<DocumentReference>
     */
    public static Task<DocumentReference> writeNewPost(int type, String title, String content, int photoNum, int summaryBuildingType, ArrayList<GeoPoint> locationList, String writerId, Timestamp writeTime, boolean isFinished) {
        Post newPost = new Post(type, title, content, photoNum, summaryBuildingType, locationList, writerId, writeTime, isFinished);
        return getFirestoreInstance().collection("post").add(newPost);
    }

    /**
     * 기존의 Post를 수정한다
     * @param postId 기존 Post ID
     * @param post Post 객체
     * @return Task<Void>
     */
    public static Task<Void> updatePost(String postId, Post post) {
        return getFirestoreInstance().collection("post").document(postId).set(post);
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

    /**
     * 게시글을 불러오는 Task를 실행한다
     * @author Taehyun Park, Minjae Seon
     * @param type 게시물 타입
     * @param startAfter 검색 시작점 Snapshot
     * @return Task<QuerySnapshot>
     */
    public static Task<QuerySnapshot> getPostData(int type, DocumentSnapshot startAfter) {
        if(startAfter != null) {
            if (type == 0)
                return getFirestoreInstance().collection("post").orderBy("writeTime", Query.Direction.DESCENDING).limit(20).startAfter(startAfter).get();
            else
                return getFirestoreInstance().collection("post").whereEqualTo("type", type)
                        .orderBy("writeTime", Query.Direction.DESCENDING).limit(20).startAfter(startAfter).get();
        }
        else {
            if (type == 0)
                return getFirestoreInstance().collection("post").orderBy("writeTime", Query.Direction.DESCENDING).limit(20).get();
            else
                return getFirestoreInstance().collection("post").whereEqualTo("type", type)
                        .orderBy("writeTime", Query.Direction.DESCENDING).limit(20).get();
        }
    }

    /**
     * 자신이 작성한 게시글을 불러오는 Query를 생성한다
     * @author Minjae Seon
     * @param userId Firebase User ID
     * @return Query
     */
    public static Task<QuerySnapshot> getMyPostData(String userId, DocumentSnapshot startAfter) {
        if(startAfter != null)
            return getFirestoreInstance().collection("post").whereEqualTo("writerId", userId)
                    .orderBy("writeTime", Query.Direction.DESCENDING).limit(20).startAfter(startAfter).get();
        else
            return getFirestoreInstance().collection("post").whereEqualTo("writerId", userId)
                    .orderBy("writeTime", Query.Direction.DESCENDING).limit(20).get();
    }

    /**
     * 문서 ID를 통해 문서 정보를 불러온다
     * @param docId 문서 ID
     * @return Task<DocumentSnapshot>
     */
    public static Task<DocumentSnapshot> getPostDataFromId(String docId) {
        return getFirestoreInstance().collection("post").document(docId).get();
    }

    /*
     * 자신의 프로필에서 닉네임을 수정한다.
     * @author Taehyun Park
     * @param userId
     * @param nickName
     * @return Task<Void>
     */
    public static Task<Void> updateProfileNickName(String userId, String nickName){
        return getFirestoreInstance().collection("user").document(userId).update("userNickName",nickName);
    }

    /*
     * 자신의 프로필에서 사진을 수정한다.
     * @author Taehyun Park
     * @param userId
     * @param userProfileImgURL
     * @return Task<Void>
     */
    public static Task<Void> updateProfileImage(String userId, String userProfileImgURL){
        return getFirestoreInstance().collection("user").document(userId).update("userProfileImgURL",userProfileImgURL);
    }

    public static Task<DocumentReference> createChatRoom(String myId, String targetId){
        ArrayList<String> userList= new ArrayList<>(Arrays.asList(myId,targetId));
        ChatRoom chatRoom = new ChatRoom(userList);
        return getFirestoreInstance().collection("chatRoom").add(chatRoom);
    }

    public static Task<DocumentReference> sendChat(String chatId, ChatData chatData){
        return getFirestoreInstance().collection("chatRoom").document(chatId).collection("chatData").add(chatData);
    }

    public static Task<QuerySnapshot> getChatData(String chatId){
        return getFirestoreInstance().collection("chatRoom").document(chatId).collection("chatData").get();
    }

    public static Task<QuerySnapshot> searchChatRoom (String myId, String targetId){
        ArrayList<String> userList1= new ArrayList<>(Arrays.asList(myId,targetId));
        ArrayList<String> userList2= new ArrayList<>(Arrays.asList(targetId,myId));
        return getFirestoreInstance().collection("chatRoom").whereIn("chatUserId", Arrays.asList(userList1,userList2)).get();
    }

}
