package be.portal.job.services.impls;

import be.portal.job.dtos.company.requests.CompanyRequest;
import be.portal.job.dtos.company.responses.CompanyResponse;
import be.portal.job.entities.Company;
import be.portal.job.entities.CompanyAdvertiser;
import be.portal.job.entities.JobAdvertiser;
import be.portal.job.enums.AdvertiserRole;
import be.portal.job.exceptions.NotAllowedException;
import be.portal.job.exceptions.NotFoundException;
import be.portal.job.exceptions.company.CompanyNotFoundException;
import be.portal.job.repositories.CompanyAdvertiserRepository;
import be.portal.job.repositories.CompanyRepository;
import be.portal.job.repositories.JobOfferRepository;
import be.portal.job.services.ICompanyService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class CompanyServiceImpl implements ICompanyService {

    private final CompanyRepository companyRepository;
    private final CompanyAdvertiserRepository companyAdvertiserRepository;
    private final JobOfferRepository jobOfferRepository;
    private final AuthServiceImpl authService;

    @Override
    public List<CompanyResponse> getAll() {
        return companyRepository.findAll().stream()
                .map(CompanyResponse::fromEntity)
                .toList();
    }

    @Override
    public CompanyResponse getCompanyById(Long id) {
        return companyRepository.findById(id)
                .map(CompanyResponse::fromEntity)
                .orElseThrow(CompanyNotFoundException::new);
    }

    @Override
    @Transactional
    public CompanyResponse addCompany(CompanyRequest companyRequest) {
        JobAdvertiser currentUser = authService.getAuthenticatedAdvertiser();

        Company company = new Company();
        companyRequest.updateEntity(company);
        companyRepository.save(company);

        CompanyAdvertiser companyAdvertiser = new CompanyAdvertiser(AdvertiserRole.OWNER, currentUser, company);
        companyAdvertiserRepository.save(companyAdvertiser);

        return CompanyResponse.fromEntity(company);
    }

    @Override
    public CompanyResponse updateCompany(Long id, CompanyRequest companyRequest) {
        JobAdvertiser currentUser = authService.getAuthenticatedAdvertiser();

        Company company = companyRepository.findById(id).orElseThrow(CompanyNotFoundException::new);

        CompanyAdvertiser companyAdvertiser = companyAdvertiserRepository
                .findByCompanyAndAgent(company.getId(), currentUser.getId())
                .orElseThrow(() -> new NotFoundException("Agent not found."));

        if (!isOwner(companyAdvertiser) && !authService.isAdmin(currentUser)) {
            throw new NotAllowedException("You are not allowed to update this company.");
        }

        companyRequest.updateEntity(company);

        return CompanyResponse.fromEntity(companyRepository.save(company));
    }

    @Override
    @Transactional
    public CompanyResponse deleteCompany(Long id) {
        JobAdvertiser currentUser = authService.getAuthenticatedAdvertiser();

        Company company = companyRepository.findById(id).orElseThrow(CompanyNotFoundException::new);

        CompanyAdvertiser companyAdvertiser = companyAdvertiserRepository.
                findByCompanyAndAgent(company.getId(), currentUser.getId())
                .orElseThrow(() -> new NotAllowedException("You are not part of this company."));

        if (!isOwner(companyAdvertiser) && !authService.isAdmin(currentUser)) {
            throw new NotAllowedException("You are not allowed to delete this company.");
        }

        List<Long> agentsIds = companyAdvertiserRepository.findAllAgentsIdsByCompany(company.getId());

        jobOfferRepository.deleteByAgentsIds(agentsIds);
        companyAdvertiserRepository.deleteByIds(agentsIds);
        companyRepository.delete(company);

        return CompanyResponse.fromEntity(company);
    }

    private boolean isOwner(CompanyAdvertiser companyAdvertiser) {
        return companyAdvertiser.getAdvertiserRole().equals(AdvertiserRole.OWNER);
    }
}
