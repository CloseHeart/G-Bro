package kr.ac.gachon.sw.gbro.util;

import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
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
    /**
     * Firestore의 Instance를 반환한다
     * @author Minjae Seon
     * @return FirebaseFirestore Instance
     */
    public static FirebaseFirestore getFirestoreInstance() {
        return FirebaseFirestore.getInstance();
    }

    /**
     * 새로운 유저의 정보를 DB에 추가하도록 요청한다
     * @author Minjae Seon
     * @param userId Firebase UID
     * @param userEmail 유저 이메일
     * @param userNickName 유저 닉네임
     * @param fcmToken FCM Token
     * @return Task<Void>
     */
    public static Task<Void> writeNewUser(String userId, String userEmail, String userNickName, String fcmToken) {
        User newUser = new User(userEmail, userNickName, null, new Timestamp(new Date()), fcmToken);
        return getFirestoreInstance().collection("user").document(userId).set(newUser);
    }
    
    /**
     * 새로운 게시물의 정보를 DB에 추가하도록 요청한다.
     * @author Taehyun Park
     * @param newPost 새 post
     * @return Task<DocumentReference>
     */
    public static Task<DocumentReference> writeNewPost(Post newPost) {
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

    /**
     * 유저의 정보를 DB에서 제거하도록 요청한다
     * @author Minjae Seon
     * @param userId Firebase UID
     * @return Task<Void>
     */
    public static Task<Void> removeUser(String userId) {
        return getFirestoreInstance().collection("user").document(userId).delete();
    }

    /**
     * 게시물을 DB에서 삭제하도록 요청한다
     * @author Taehyun Park
     * @param postId
     * @return Task<Void>
     */
    public static Task<Void> removePost(String postId) {
        return getFirestoreInstance().collection("post").document(postId).delete();
    }

    /**
     * 유저 정보를 가져온다
     * @author Minjae Seon
     * @param userId Firebase UID
     * @return Task<DocumentSnapshot>
     */
    public static Task<DocumentSnapshot> getUserData(String userId) {
        return getFirestoreInstance().collection("user").document(userId).get();
    }

    /**
     * 게시글을 불러오는 Query를 가져온다
     * @author Taehyun Park, Minjae Seon
     * @param type 게시물 타입
     * @param startAfter 검색 시작점 Snapshot
     * @return Query
     */
    public static Query getPostData(int type, DocumentSnapshot startAfter) {
        if(startAfter != null) {
            if (type == 0)
                return getFirestoreInstance().collection("post").orderBy("writeTime", Query.Direction.DESCENDING).limit(20).startAfter(startAfter);
            else
                return getFirestoreInstance().collection("post").whereEqualTo("type", type)
                        .orderBy("writeTime", Query.Direction.DESCENDING).limit(20).startAfter(startAfter);
        }
        else {
            if (type == 0)
                return getFirestoreInstance().collection("post").orderBy("writeTime", Query.Direction.DESCENDING).limit(20);
            else
                return getFirestoreInstance().collection("post").whereEqualTo("type", type)
                        .orderBy("writeTime", Query.Direction.DESCENDING).limit(20);
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

    /**
     * 자신의 프로필에서 닉네임을 수정한다.
     * @author Taehyun Park
     * @param userId
     * @param nickName
     * @return Task<Void>
     */
    public static Task<Void> updateProfileNickName(String userId, String nickName){
        return getFirestoreInstance().collection("user").document(userId).update("userNickName",nickName);
    }

    /**
     * 자신의 프로필에서 사진을 수정한다.
     * @author Taehyun Park
     * @param userId
     * @param userProfileImgURL
     * @return Task<Void>
     */
    public static Task<Void> updateProfileImage(String userId, String userProfileImgURL){
        return getFirestoreInstance().collection("user").document(userId).update("userProfileImgURL",userProfileImgURL);
    }

    /**
     * 새로운 채팅방을 생성한다
     * @param myId 사용자 ID
     * @param targetId 상대방 ID
     * @return Task<DocumentReference>
     */
    public static Task<DocumentReference> createChatRoom(String myId, String targetId){
        ArrayList<String> userList= new ArrayList<>(Arrays.asList(myId,targetId));
        ChatRoom chatRoom = new ChatRoom(userList);
        return getFirestoreInstance().collection("chatRoom").add(chatRoom);
    }

    /**
     * 채팅을 전송한다
     * @param chatId 채팅방 ID
     * @param chatData ChatData
     * @return Task<DocumentReference>
     */
    public static Task<DocumentReference> sendChat(String chatId, ChatData chatData){
        return getFirestoreInstance().collection("chatRoom").document(chatId).collection("chatData").add(chatData);
    }

    /**
     * 기존에 채팅방이 존재하는지 검색한다
     * @param myId 사용자 ID
     * @param targetId 상대방 ID
     * @return Task<QuerySnapshot>
     */
    public static Task<QuerySnapshot> searchChatRoom (String myId, String targetId){
        ArrayList<String> userList1= new ArrayList<>(Arrays.asList(myId,targetId));
        ArrayList<String> userList2= new ArrayList<>(Arrays.asList(targetId,myId));
        return getFirestoreInstance().collection("chatRoom").whereIn("chatUserId", Arrays.asList(userList1,userList2)).get();
    }

    /**
     * 채팅 데이터를 가져오는 Query를 가져온다
     * @param chatRoomId Chat Room ID
     * @return Query
     */
    public static Query getChatDataQuery(String chatRoomId) {
        return getFirestoreInstance().collection("chatRoom").document(chatRoomId).collection("chatData").orderBy("date", Query.Direction.ASCENDING);
    }

    /**
     * 완료되지 않은 모든 Post를 불러온다
     * @param postType Post Type
     *                 0 - 전체
     *                 1 - 분실물
     *                 2 - 습득물
     * @return Task<QuerySnapshot>
     */
    public static Task<QuerySnapshot> getUnfinishedPost(int postType) {
        if(postType == 0) {
            return getFirestoreInstance().collection("post")
                    .whereEqualTo("finished", false).get();
        }
        else {
            return getFirestoreInstance().collection("post")
                    .whereEqualTo("type", postType)
                    .whereEqualTo("finished", false).get();
        }
    }

    /**
     * 해당하는 건물에 완료되지 않은 모든 Post를 불러온다
     * @param postType Post Type
     *                 0 - 전체
     *                 1 - 분실물
     *                 2 - 습득물
     * @param buildingNum 건물 번호
     * @return Task<QuerySnapshot>
     */
    public static Task<QuerySnapshot> getUnfinishedBuildingPost(int postType, int buildingNum) {
        if(postType == 0) {
            return getFirestoreInstance().collection("post")
                    .whereEqualTo("summaryBuildingType", buildingNum)
                    .whereEqualTo("finished", false).get();
        }
        else {
            return getFirestoreInstance().collection("post")
                    .whereEqualTo("summaryBuildingType", buildingNum)
                    .whereEqualTo("type", postType)
                    .whereEqualTo("finished", false).get();
        }
    }

    public static Query getMyChatRoom(String userId) {
         return getFirestoreInstance().collection("chatRoom").whereArrayContains("chatUserId", userId);
    }

    /**
     * 유저의 FCM Token 정보를 업데이트 한다
     * @param userId User UID
     * @param fcmToken Token
     * @return Task<Void>
     */
    public static Task<Void> setUserFcmToken(String userId, String fcmToken) {
        return getFirestoreInstance().collection("user").document(userId).update("fcmToken", fcmToken);
    }

    public static Task<QuerySnapshot> getBuildingPost(int buildingId){
        return getFirestoreInstance().collection("post").whereEqualTo("summaryBuildingType", buildingId).get();
    }

}
