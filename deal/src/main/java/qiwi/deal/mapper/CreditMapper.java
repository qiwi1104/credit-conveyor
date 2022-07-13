package qiwi.deal.mapper;

import org.mapstruct.Mapper;
import qiwi.deal.dto.CreditDTO;
import qiwi.deal.entity.Credit;

@Mapper(componentModel = "spring")
public interface CreditMapper {
    Credit mapToEntity(CreditDTO creditDTO);
}
