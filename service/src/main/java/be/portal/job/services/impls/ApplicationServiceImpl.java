package be.portal.job.services.impls;

import be.portal.job.dtos.application.requests.ApplicationRequest;
import be.portal.job.dtos.application.responses.ApplicationResponse;
import be.portal.job.entities.Application;
import be.portal.job.entities.JobOffer;
import be.portal.job.entities.JobSeeker;
import be.portal.job.exceptions.NotFoundException;
import be.portal.job.repositories.ApplicationRepository;
import be.portal.job.repositories.JobOfferRepository;
import be.portal.job.services.IApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ApplicationServiceImpl implements IApplicationService {

    private final ApplicationRepository applicationRepository;
    private final JobOfferRepository jobOfferRepository;

    @Override
    public List<ApplicationResponse> getAll() {
        return applicationRepository.findAll()
                .stream()
                .map(ApplicationResponse ::fromEntity)
                .toList();
    }

    @Override
    public ApplicationResponse getApplicationById(Long id) {
        return applicationRepository.findById(id)
                .map(ApplicationResponse::fromEntity)
                .orElseThrow();
    }

    @Override
    public ApplicationResponse addApplication(ApplicationRequest applicationRequest) {

        JobSeeker jobSeeker = (JobSeeker) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
        JobOffer jobOffer = jobOfferRepository.findById(applicationRequest.jobOfferId())
                .orElseThrow(()-> new NotFoundException("Job offer not found"));
        Application application = applicationRequest.toEntity(jobSeeker, jobOffer);

        return ApplicationResponse.fromEntity(applicationRepository.save(application));
    }

    @Override
    public ApplicationResponse updateApplication(Long id, ApplicationRequest applicationRequest) {
        Application application = applicationRepository.findById(id)
                .orElseThrow(()-> new NotFoundException("Application not found: " + id ));

        JobSeeker jobSeeker = (JobSeeker) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
        JobOffer jobOffer = jobOfferRepository.findById(applicationRequest.jobOfferId())
                .orElseThrow(()-> new NotFoundException("Job offer not found"));

        application.setApplyDate(applicationRequest.apply_date());
        application.setApplicationStatus(applicationRequest.applicationStatus());
        application.setJobSeeker(jobSeeker);
        application.setJobOffer(jobOffer);

        return ApplicationResponse.fromEntity(applicationRepository.save(application));
    }

    @Override
    public ApplicationResponse deleteApplication(Long id) {

        Application application = applicationRepository.findById(id)
                .orElseThrow(()-> new NotFoundException("Application not found"));

        applicationRepository.deleteById(id);

        return ApplicationResponse.fromEntity(application);
    }
}
