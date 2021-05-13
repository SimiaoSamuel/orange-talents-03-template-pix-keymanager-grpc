package br.com.zup.edu

import br.com.zup.edu.dto.CreatePixKeyRequest
import br.com.zup.edu.dto.NovaChavePix
import br.com.zup.edu.httpclient.BacenClient
import br.com.zup.edu.httpclient.ERPClient
import br.com.zup.edu.model.Pix
import br.com.zup.edu.repository.PixRepository
import io.micronaut.context.annotation.Requires
import io.micronaut.context.annotation.Value
import io.micronaut.scheduling.annotation.Scheduled
import java.lang.RuntimeException
import javax.inject.Singleton

@Singleton
//@Requires(notEnv = ["test"])
class BacenManager(
    val bacenClient: BacenClient,
    val repository: PixRepository,
) {

    @Scheduled(fixedDelay = "5000ms", initialDelay = "\${periodictask.initialdelay:10m}")
    fun syncPix(){
        bacenClient.listAllPix()?.pixKeys?.let {
            val pixsNoBancoDeDados = repository.findAll().toHashSet()
            it.map { pixsNoBancoDeDados.add(it.toPixModel()) }
            repository.updateAll(pixsNoBancoDeDados)
        }
    }

    @Scheduled(fixedDelay = "7000ms", initialDelay = "\${periodictask.balancerdelay:10m}")
    fun syncPixBalancer(){
        val pixInDatabaseToSaveInBacen = bacenClient.listAllPix()?.pixKeys?.let {
            val pixsNoBancoDeDados = repository.findAll().toHashSet()
            val bacen = it.map { it.toPixModel() }.toSet()
            pixsNoBancoDeDados.minus(bacen)
        }

        pixInDatabaseToSaveInBacen?.let {
            repository.deleteAll(it)
        }
    }
}