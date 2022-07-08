package qiwi.deal.mapper;

import org.mapstruct.Mapper;
import qiwi.deal.entity.PaymentScheduleElement;

@Mapper(componentModel = "spring")
public interface PaymentScheduleElementMapper {
    PaymentScheduleElement mapToEntity(qiwi.conveyor.dto.PaymentScheduleElement paymentScheduleElement);
}
