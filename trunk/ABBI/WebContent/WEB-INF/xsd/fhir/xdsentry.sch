<?xml version="1.0" encoding="UTF-8"?>
<sch:schema xmlns:sch="http://purl.oclc.org/dsdl/schematron">
  <sch:ns prefix="f" uri="http://hl7.org/fhir"/>
  <sch:pattern>
    <sch:title>XdsEntry</sch:title>
    <sch:rule context="/f:XdsEntry">
      <sch:assert test="exists(f:url) or exists(f:repositoryId) ">A rul or a repositoryId must be provided</sch:assert>
    </sch:rule>
    <sch:rule context="/f:XdsEntry/f:folder">
      <sch:assert test="not(contains(f:version, ':')) or exists(f:id)">If the version url is provided, and local, then a logical id must be provided too</sch:assert>
    </sch:rule>
    <sch:rule context="/f:XdsEntry/f:patientInfo">
      <sch:assert test="not(contains(f:version, ':')) or exists(f:id)">If the version url is provided, and local, then a logical id must be provided too</sch:assert>
    </sch:rule>
    <sch:rule context="/f:XdsEntry/f:author">
      <sch:assert test="exists(f:name) or exists(f:id) or exists(f:contact) or exists(f:institution)">At least a name, id, contact, or institution sub-attribute shall be present</sch:assert>
    </sch:rule>
    <sch:rule context="/f:XdsEntry/f:author/f:contact">
      <sch:assert test="not(exists(f:value)) or exists(f:system)">A system is required if a value is provided.</sch:assert>
    </sch:rule>
    <sch:rule context="/f:XdsEntry/f:authenticator">
      <sch:assert test="exists(f:name) or exists(f:id) or exists(f:contact) or exists(f:institution)">A name or id must be provided</sch:assert>
    </sch:rule>
    <sch:rule context="/f:XdsEntry/f:extension">
      <sch:assert test="not(exists(f:extension/f:ref))">Nexted Extensions cannot have references</sch:assert>
      <sch:assert test="exists(f:*[starts-with(local-name(.), 'value')]) != exists(f:extension)">Either a value or nested extensions must be present, but not both</sch:assert>
    </sch:rule>
    <sch:rule context="/f:XdsEntry/f:extension//f:extension">
      <sch:assert test="not(exists(f:extension/f:ref))">Nexted Extensions cannot have references</sch:assert>
      <sch:assert test="exists(f:*[starts-with(local-name(.), 'value')]) != exists(f:extension)">Either a value or nested extensions must be present, but not both</sch:assert>
    </sch:rule>
  </sch:pattern>
</sch:schema>
