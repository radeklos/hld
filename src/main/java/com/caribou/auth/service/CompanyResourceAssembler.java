package com.caribou.auth.service;

import com.caribou.company.domain.Company;
import com.caribou.company.rest.CompanyRestController;
import com.caribou.company.rest.dto.CompanyDto;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityLinks;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.RelProvider;
import org.springframework.stereotype.Service;

@Service
public class CompanyResourceAssembler extends EmbeddableResourceAssemblerSupport<Company, CompanyDto, CompanyRestController> {

    private ModelMapper modelMapper = new ModelMapper();

    @Autowired
    public CompanyResourceAssembler(final EntityLinks entityLinks, final RelProvider relProvider) {
        super(entityLinks, relProvider, CompanyRestController.class, CompanyDto.class);
    }

    @Override
    public Link linkToSingleResource(Company publisher) {
        return entityLinks.linkToSingleResource(CompanyRestController.class, getId(publisher));
    }

    @Override
    protected CompanyDto instantiateResource(Company entity) {
        return modelMapper.map(entity, CompanyDto.class);
    }

    @Override
    public CompanyDto toResource(Company entity) {
        return createResourceWithId(getId(entity), entity);
    }

}
