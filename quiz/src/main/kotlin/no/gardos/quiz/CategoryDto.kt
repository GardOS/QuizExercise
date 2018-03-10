package no.gardos.quiz

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import java.io.Serializable

@ApiModel("DTO for Category. It represent an Category entity")
data class CategoryDto(

		@ApiModelProperty("Category id")
		var id: String? = null,

		@ApiModelProperty("The name of the Category")
		var name: String? = null

) : Serializable //Todo: check if needed