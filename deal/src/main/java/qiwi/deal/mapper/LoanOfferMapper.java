package qiwi.deal.mapper;

import org.mapstruct.Mapper;
import qiwi.deal.dto.LoanOfferDTO;
import qiwi.deal.entity.LoanOffer;

@Mapper(componentModel = "spring")
public interface LoanOfferMapper {
    LoanOffer mapToEntity(LoanOfferDTO loanOfferDTO);
}
