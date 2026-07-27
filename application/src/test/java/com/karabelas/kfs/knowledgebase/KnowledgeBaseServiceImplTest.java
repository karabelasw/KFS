package com.karabelas.kfs.knowledgebase;

import com.karabelas.kfs.common.ResourceNotFoundException;
import com.karabelas.kfs.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link KnowledgeBaseServiceImpl}, per the anticipated
 * coverage listed in the prior skeleton:
 *   - auto-provisioning the default/INBOX KB
 *   - a user cannot end up with zero or multiple default KBs
 */
@ExtendWith(MockitoExtension.class)
class KnowledgeBaseServiceImplTest {

    @Mock
    private KnowledgeBaseRepository knowledgeBaseRepository;

    @Mock
    private UserService userService;

    private KnowledgeBaseServiceImpl knowledgeBaseService;

    @BeforeEach
    void setUp() {
        knowledgeBaseService = new KnowledgeBaseServiceImpl(knowledgeBaseRepository, userService);
    }

    @Test
    void findById_returnsDtoWithResolvedOwnerUsername() {
        KnowledgeBase kb = newKb(1L, 10L, "Inbox", true);
        when(knowledgeBaseRepository.findById(1L)).thenReturn(Optional.of(kb));
        when(userService.findUsernameById(10L)).thenReturn("billy");

        KnowledgeBaseDto dto = knowledgeBaseService.findById(1L);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getOwnerUsername()).isEqualTo("billy");
        assertThat(dto.isDefault()).isTrue();
    }

    @Test
    void findById_throwsResourceNotFoundException_whenMissing() {
        when(knowledgeBaseRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> knowledgeBaseService.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void createDefaultKnowledgeBase_createsExactlyOneDefaultPerOwner() {
        when(knowledgeBaseRepository.findByOwnerIdAndIsDefaultTrue(10L)).thenReturn(Optional.empty());
        when(knowledgeBaseRepository.save(any(KnowledgeBase.class)))
                .thenAnswer(invocation -> {
                    KnowledgeBase kb = invocation.getArgument(0);
                    kb.setId(1L);
                    return kb;
                });
        when(userService.findUsernameById(10L)).thenReturn("billy");

        KnowledgeBaseDto dto = knowledgeBaseService.createDefaultKnowledgeBase(10L);

        assertThat(dto.isDefault()).isTrue();
        assertThat(dto.getOwnerId()).isEqualTo(10L);

        ArgumentCaptor<KnowledgeBase> captor = ArgumentCaptor.forClass(KnowledgeBase.class);
        verify(knowledgeBaseRepository).save(captor.capture());
        assertThat(captor.getValue().isDefault()).isTrue();
    }

    @Test
    void createDefaultKnowledgeBase_rejectsSecondDefaultForSameOwner() {
        when(knowledgeBaseRepository.findByOwnerIdAndIsDefaultTrue(10L))
                .thenReturn(Optional.of(newKb(1L, 10L, "Inbox", true)));

        assertThatThrownBy(() -> knowledgeBaseService.createDefaultKnowledgeBase(10L))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void create_neverMarksARegularKnowledgeBaseAsDefault() {
        when(knowledgeBaseRepository.save(any(KnowledgeBase.class)))
                .thenAnswer(invocation -> {
                    KnowledgeBase kb = invocation.getArgument(0);
                    kb.setId(2L);
                    return kb;
                });
        when(userService.findUsernameById(anyLong())).thenReturn("billy");

        KnowledgeBaseDto request = new KnowledgeBaseDto();
        request.setOwnerId(10L);
        request.setName("Project X");

        KnowledgeBaseDto result = knowledgeBaseService.create(request);

        assertThat(result.isDefault()).isFalse();
    }

    @Test
    void findByOwnerId_returnsAllKnowledgeBasesForThatOwner() {
        when(knowledgeBaseRepository.findByOwnerId(10L))
                .thenReturn(List.of(newKb(1L, 10L, "Inbox", true), newKb(2L, 10L, "Project X", false)));
        when(userService.findUsernameById(10L)).thenReturn("billy");

        List<KnowledgeBaseDto> result = knowledgeBaseService.findByOwnerId(10L);

        assertThat(result).hasSize(2);
    }

    private KnowledgeBase newKb(Long id, Long ownerId, String name, boolean isDefault) {
        KnowledgeBase kb = new KnowledgeBase();
        kb.setId(id);
        kb.setOwnerId(ownerId);
        kb.setName(name);
        kb.setDefault(isDefault);
        return kb;
    }
}
