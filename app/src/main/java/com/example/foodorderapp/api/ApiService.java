package com.example.foodorderapp.api;

import com.example.foodorderapp.model.DistrictModel;
import com.example.foodorderapp.model.ProvinceModel;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ApiService {
    @GET("api/p/")
    Call<List<ProvinceModel>> getProvinces();

    @GET("api/p/{code}?depth=2")
    Call<ProvinceDetailResponse> getDistricts(@Path("code") int provinceCode);

    @GET("api/d/{code}?depth=2")
    Call<DistrictModel> getWards(@Path("code") int districtCode);
}
