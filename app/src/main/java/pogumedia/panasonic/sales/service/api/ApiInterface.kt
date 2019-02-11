package pogumedia.panasonic.sales.service.api

import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*


interface ApiInterface {


    @FormUrlEncoded
    @POST("login_sales")
    fun login(
            @Field("email") username: String,
            @Field("password") password: String,
            @Field("role") role: String,
            @Field("reg_id") regId: String?
    ): Observable<ResponseBody>

    @FormUrlEncoded
    @POST("forget_password")
    fun forgot(
            @Field("email") email: String
    ): Observable<ResponseBody>


    @FormUrlEncoded
    @POST("home_sales")
    fun stores(
            @Field("id_users") id_users: Int,
            @Field("offset") offset: Int,
            @Field("limit") limit: Int
    ): Observable<ResponseBody>

    @FormUrlEncoded
    @POST("pencarian")
    fun storesSearch(
            @Field("id_users") id_users: Int,
            @Field("cari") query: String?,
            @Field("offset") offset: Int,
            @Field("limit") limit: Int
    ): Observable<ResponseBody>

    @FormUrlEncoded
    @POST("tambah_toko")
    fun submitStore(
            @Field("id_users") id_users: Int?,
            @Field("nama") nama: String?,
            @Field("address") address: String?,
            @Field("pic") pic: String?,
            @Field("telp") telp: String?,
            @Field("latitude") latitude: String?,
            @Field("longitude") longitude: String?,
            @Field("id_kabupaten") id_kabupaten: Int?,
            @Field("nama_product") nama_product: String?,
            @Field("created_at") created_at: String?
    ): Observable<ResponseBody>


    @FormUrlEncoded
    @POST("edit_password")
    fun updatePassword(
            @Field("id") id: Int?,
            @Field("password") password: String?,
            @Field("password_baru") password_baru: String?
    ): Observable<ResponseBody>

    @FormUrlEncoded
    @POST("list_area")
    fun areas(
            @Field("id_user") id: Int?
    ): Observable<ResponseBody>

    @POST("list_product")
    fun products(): Observable<ResponseBody>

    @POST("list_project")
    fun projectBanner(): Observable<ResponseBody>

    @POST("list_signboard")
    fun signBoard(): Observable<ResponseBody>

    @POST("list_request")
    fun typeRequest(): Observable<ResponseBody>

    @Multipart
    @POST("input_survey")
    fun submitStoreSurvey(
            @Query("id_outlet") id_outlet: Int?,
            @Query("id_users") id_users: Int?,
            @Query("laporan_latitude") lat: String?,
            @Query("laporan_longitude") long: String?,
            @Query("nama_produk") nama_produk: String?,
            @Query("nama_project") nama_project: String?,
            @Query("ukuran_banner") ukuran_banner: String?,
            @Query("signboard") signboard: String?,
            @Query("type_request") type_request: String?,
            @Query("note") note: String?,
            @Query("in_radius") in_radius: String?,
            @Query("created_at") created_at: String?,
            @Part photo: MultipartBody.Part

    ): Observable<ResponseBody>


    @FormUrlEncoded
    @POST("riwayat_survey")
    fun historySurvey(
            @Field("id_user") userId: Int,
            @Field("offset") offset: Int,
            @Field("limit") limit: Int
    ): Observable<ResponseBody>

    @FormUrlEncoded
    @POST("pencarian_riwayat_sales")
    fun surveySearch(
            @Field("id_users") userId: Int,
            @Field("cari") query: String?,
            @Field("offset") offset: Int,
            @Field("limit") limit: Int
    ): Observable<ResponseBody>


    @FormUrlEncoded
    @POST("tambah_toko")
    fun submitStoreOffline(
            @Field("id_users") id_users: Int?,
            @Field("nama") nama: String?,
            @Field("address") address: String?,
            @Field("pic") pic: String?,
            @Field("telp") telp: String?,
            @Field("latitude") latitude: String?,
            @Field("longitude") longitude: String?,
            @Field("id_kabupaten") id_kabupaten: Int?,
            @Field("nama_produk") nama_produk: String?,
            @Field("created_at") created_at: String?
    ): Call<ResponseBody>

    @Multipart
    @POST("input_survey")
    fun submitStoreSurveyOffline(
            @Query("id_outlet") id_outlet: Int?,
            @Query("id_users") id_users: Int?,
            @Query("laporan_latitude") lat: String?,
            @Query("laporan_longitude") long: String?,
            @Query("nama_produk") nama_produk: String?,
            @Query("nama_project") nama_project: String?,
            @Query("ukuran_banner") ukuran_banner: String?,
            @Query("signboard") signboard: String?,
            @Query("type_request") type_request: String?,
            @Query("note") note: String?,
            @Query("in_radius") in_radius: String?,
            @Query("created_at") created_at: String?,
            @Part photo: MultipartBody.Part

    ): Call<ResponseBody>

}