package no.gardos.schema

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty

@ApiModel("DTO for Category. It represent a Category entity")
data class CategoryDto(
		@ApiModelProperty("The name of the Category")
		var name: String? = null,

		@ApiModelProperty("Category id")
		var id: String? = null
)