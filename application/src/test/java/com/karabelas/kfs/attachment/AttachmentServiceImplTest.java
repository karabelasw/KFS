package com.karabelas.kfs.attachment;

import com.karabelas.kfs.common.DuplicateResourceException;
import com.karabelas.kfs.common.ResourceNotFoundException;
import com.karabelas.kfs.entry.EntryDto;
import com.karabelas.kfs.entry.EntryService;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** Unit tests for {@link AttachmentServiceImpl} — the LINK-only first cut. */
@ExtendWith(MockitoExtension.class)
class AttachmentServiceImplTest {

    @Mock
    private AttachmentRepository attachmentRepository;

    @Mock
    private AttachmentTypeRepository attachmentTypeRepository;

    @Mock
    private EntryService entryService;

    private AttachmentServiceImpl attachmentService;

    @BeforeEach
    void setUp() {
        attachmentService = new AttachmentServiceImpl(attachmentRepository, attachmentTypeRepository, entryService);
    }

    @Test
    void addLink_validatesEntryExists_beforeSaving() {
        when(entryService.findById(100L)).thenReturn(new EntryDto());
        when(attachmentRepository.findByEntryIdAndDisplayName(100L, "Demo video")).thenReturn(Optional.empty());
        when(attachmentTypeRepository.findByCode("LINK")).thenReturn(Optional.of(newType(2L, "LINK")));
        when(attachmentRepository.save(any(Attachment.class))).thenAnswer(invocation -> {
            Attachment a = invocation.getArgument(0);
            a.setId(5L);
            return a;
        });

        AttachmentDto result = attachmentService.addLink(100L, "Demo video", "https://youtube.com/watch?v=abc");

        assertThat(result.getId()).isEqualTo(5L);
        verify(entryService).findById(100L);
    }

    @Test
    void addLink_propagatesResourceNotFoundException_whenEntryMissing() {
        when(entryService.findById(999L)).thenThrow(new ResourceNotFoundException("Entry 999 not found"));

        assertThatThrownBy(() -> attachmentService.addLink(999L, "Demo video", "https://youtube.com/watch?v=abc"))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(attachmentRepository, never()).save(any());
    }

    @Test
    void addLink_rejectsDuplicateDisplayNameWithinSameEntry() {
        when(entryService.findById(100L)).thenReturn(new EntryDto());
        when(attachmentRepository.findByEntryIdAndDisplayName(100L, "Demo video"))
                .thenReturn(Optional.of(new Attachment()));

        assertThatThrownBy(() -> attachmentService.addLink(100L, "Demo video", "https://youtube.com/watch?v=abc"))
                .isInstanceOf(DuplicateResourceException.class);

        verify(attachmentRepository, never()).save(any());
    }

    @Test
    void addLink_setsExternalUrl_andLeavesFileIdNull() {
        when(entryService.findById(100L)).thenReturn(new EntryDto());
        when(attachmentRepository.findByEntryIdAndDisplayName(100L, "Demo video")).thenReturn(Optional.empty());
        when(attachmentTypeRepository.findByCode("LINK")).thenReturn(Optional.of(newType(2L, "LINK")));
        when(attachmentRepository.save(any(Attachment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        attachmentService.addLink(100L, "Demo video", "https://youtube.com/watch?v=abc");

        ArgumentCaptor<Attachment> captor = ArgumentCaptor.forClass(Attachment.class);
        verify(attachmentRepository).save(captor.capture());
        assertThat(captor.getValue().getExternalUrl()).isEqualTo("https://youtube.com/watch?v=abc");
        assertThat(captor.getValue().getFileId()).isNull();
        assertThat(captor.getValue().getAttachmentTypeId()).isEqualTo(2L);
    }

    @Test
    void addLink_throwsIllegalStateException_whenLinkTypeNotSeeded() {
        when(entryService.findById(100L)).thenReturn(new EntryDto());
        when(attachmentRepository.findByEntryIdAndDisplayName(100L, "Demo video")).thenReturn(Optional.empty());
        when(attachmentTypeRepository.findByCode("LINK")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> attachmentService.addLink(100L, "Demo video", "https://youtube.com/watch?v=abc"))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void findByEntryId_returnsAttachmentsForThatEntry() {
        Attachment a = new Attachment();
        a.setId(1L);
        a.setEntryId(100L);
        a.setDisplayName("Demo video");
        a.setExternalUrl("https://youtube.com/watch?v=abc");
        when(attachmentRepository.findByEntryId(100L)).thenReturn(List.of(a));

        List<AttachmentDto> result = attachmentService.findByEntryId(100L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDisplayName()).isEqualTo("Demo video");
    }

    @Test
    void removeAttachment_deletesWhenFound() {
        Attachment a = new Attachment();
        a.setId(1L);
        when(attachmentRepository.findById(1L)).thenReturn(Optional.of(a));

        attachmentService.removeAttachment(1L);

        verify(attachmentRepository, times(1)).delete(a);
    }

    @Test
    void removeAttachment_throwsResourceNotFoundException_whenMissing() {
        when(attachmentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> attachmentService.removeAttachment(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    private AttachmentType newType(Long id, String code) {
        AttachmentType type = new AttachmentType();
        type.setId(id);
        type.setCode(code);
        return type;
    }
}
