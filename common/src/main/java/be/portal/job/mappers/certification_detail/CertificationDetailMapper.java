package be.portal.job.mappers.certification_detail;

import be.portal.job.dtos.certification_detail.responses.CertificationDetailResponse;
import be.portal.job.entities.CertificationDetail;
import be.portal.job.entities.JobSeeker;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CertificationDetailMapper {

    @Mapping(target = "id", ignore= true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(source = "jobSeeker", target = "jobSeeker")
    CertificationDetail toEntity(JobSeeker jobSeeker);

    CertificationDetailResponse fromEntity(CertificationDetail certificationDetail);
}
