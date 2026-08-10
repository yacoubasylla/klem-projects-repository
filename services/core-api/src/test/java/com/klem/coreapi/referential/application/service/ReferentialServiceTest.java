package com.klem.coreapi.referential.application.service;

import com.klem.coreapi.referential.application.port.ReferentialEntryRepository;
import com.klem.coreapi.referential.application.port.ReferentialListRepository;
import com.klem.coreapi.referential.domain.exception.ReferentialListNotFoundException;
import com.klem.coreapi.referential.domain.model.ReferentialEntry;
import com.klem.coreapi.referential.domain.model.ReferentialList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReferentialServiceTest {

    @Mock
    private ReferentialListRepository listRepository;

    @Mock
    private ReferentialEntryRepository entryRepository;

    private ReferentialService referentialService;

    @BeforeEach
    void setUp() {
        referentialService = new ReferentialService(listRepository, entryRepository);
    }

    @Test
    void getList_returns_list_when_found() {
        ReferentialList countries = ReferentialList.create("countries", "Pays");
        when(listRepository.findByCode("countries")).thenReturn(Optional.of(countries));

        assertThat(referentialService.getList("countries")).isSameAs(countries);
    }

    @Test
    void getList_throws_when_code_unknown() {
        when(listRepository.findByCode("inconnu")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> referentialService.getList("inconnu"))
                .isInstanceOf(ReferentialListNotFoundException.class);
    }

    @Test
    void getEntries_returns_entries_ordered() {
        ReferentialList countries = ReferentialList.create("countries", "Pays");
        ReferentialEntry ci = ReferentialEntry.create(countries.getId(), "CI", "Côte d'Ivoire", 1);
        when(entryRepository.findByListIdOrderBySortOrderAsc(countries.getId())).thenReturn(List.of(ci));

        assertThat(referentialService.getEntries(countries.getId())).containsExactly(ci);
    }
}
