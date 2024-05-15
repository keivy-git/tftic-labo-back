package be.portal.job.services.impls;

import be.portal.job.entities.Role;
import be.portal.job.repositories.RoleRepository;
import be.portal.job.services.IRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements IRoleService {

    private final RoleRepository roleRepository;

    @Override
    public List<Role> getRoles() {
        return roleRepository.findAll();
    }

    @Override
    public Optional<Role> getRoleByName(String name) {
        Optional<Role> role = roleRepository.findByName(name);

        return role;
    }

    @Override
    public Optional<Role> getRoleById(Long id) {
        Optional<Role> role = roleRepository.findById(id);
        return Optional.ofNullable(roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Role not found with id: " + id)));
    }

    @Override
    public Role addRole(Role role) {
        return roleRepository.save(role);
    }

    @Override
    public Role updateRole(Long id, Role role) {
        Role existingRole = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Role not found with id: " + id));

        existingRole.setName(role.getName());
        existingRole.setDescription(role.getDescription());
        existingRole.setUsers(role.getUsers());

        return roleRepository.save(existingRole); // Save the updated role
    }

    @Override
    public void deleteRole(Long id) {
        if (!roleRepository.existsById(id)) throw new RuntimeException("Role not found with id: " + id);
        }
    }

