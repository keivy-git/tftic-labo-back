package be.portal.job.utils.dataInitializer;

import be.portal.job.entities.JobFunction;
import be.portal.job.repositories.JobFunctionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Order(7)
public class JobFunctionInit implements CommandLineRunner {

    private final JobFunctionRepository jobFunctionRepository;

    @Override
    public void run(String... args) throws Exception {
        // Créer quelques fonctions d'emploi avec des valeurs arbitraires
        List<JobFunction> jobFunctions = List.of(
                new JobFunction("Software Developer", "Développeur Logiciel"),
                new JobFunction("Marketing Manager", "Responsable Marketing"),
                new JobFunction("Human Resources Specialist", "Spécialiste des Ressources Humaines")
        );

        jobFunctionRepository.saveAll(jobFunctions);
    }
}