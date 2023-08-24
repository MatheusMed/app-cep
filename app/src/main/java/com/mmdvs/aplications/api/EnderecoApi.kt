package com.mmdvs.aplications.api

import com.mmdvs.aplications.models.EnderecoModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface EnderecoApi {
    @GET("/ws/{cep}/json/")
    suspend fun recuperarEndereco(
        @Path("cep") cepDigitado: String
    ) : Response<EnderecoModel>

}