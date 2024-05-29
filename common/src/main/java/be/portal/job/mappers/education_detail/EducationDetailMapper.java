package be.portal.job.mappers.education_detail;

import be.portal.job.dtos.education_detail.requests.EducationDetailAddRequest;
import be.portal.job.dtos.education_detail.responses.EducationDetailResponse;
import be.portal.job.entities.EducationDetail;
import be.portal.job.entities.JobSeeker;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EducationDetailMapper {

    @Mapping(target= "id", ignore= true)
    @Mapping(source = "jobSeeker", target = "jobSeeker")

    EducationDetail toEntity(EducationDetailAddRequest addRequest, JobSeeker jobSeeker);
    EducationDetailResponse fromEntity(EducationDetail educationDetail);
}
