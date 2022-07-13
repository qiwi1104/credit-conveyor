package qiwi.deal.mapper;

import org.mapstruct.Mapper;
import qiwi.deal.dto.EmploymentDTO;
import qiwi.deal.entity.Employment;

@Mapper(componentModel = "spring")
public interface EmploymentMapper {
    Employment mapToEntity(EmploymentDTO employmentDTO);
}
