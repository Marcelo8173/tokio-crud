package com.marcelo.tokiomarine.tokiomarine.DTOs;

import java.util.List;

public record ListDefaultResponseDTO(List<?> content, int pageSize, int pageNumber) {
}
