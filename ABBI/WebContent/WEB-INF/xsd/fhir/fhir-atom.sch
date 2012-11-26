<?xml version="1.0" encoding="UTF-8"?>
<sch:schema xmlns:sch="http://purl.oclc.org/dsdl/schematron">
  <sch:ns prefix="f" uri="http://hl7.org/fhir"/>
  <sch:ns prefix="a" uri="http://www.w3.org/2005/Atom"/>
  <sch:pattern>
    <sch:title>Provenance</sch:title>
    <sch:rule context="/a:feed/a:entry/a:content/f:Provenance/f:target">
      <sch:assert test="not(contains(f:version, ':')) or exists(f:id)">If the version url is provided, and local, then a logical id must be provided too</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:Provenance/f:activity/f:reason">
      <sch:assert test="not(exists(f:primary)) or count(f:coding[@id=current()/f:primary])=1">If a primary reference is present, it must point to one of the codings</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:Provenance/f:activity/f:location/f:type">
      <sch:assert test="not(exists(f:primary)) or count(f:coding[@id=current()/f:primary])=1">If a primary reference is present, it must point to one of the codings</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:Provenance/f:extension">
      <sch:assert test="not(exists(f:extension/f:ref))">Nexted Extensions cannot have references</sch:assert>
      <sch:assert test="exists(f:*[starts-with(local-name(.), 'value')]) != exists(f:extension)">Either a value or nested extensions must be present, but not both</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:Provenance/f:extension//f:extension">
      <sch:assert test="not(exists(f:extension/f:ref))">Nexted Extensions cannot have references</sch:assert>
      <sch:assert test="exists(f:*[starts-with(local-name(.), 'value')]) != exists(f:extension)">Either a value or nested extensions must be present, but not both</sch:assert>
    </sch:rule>
    </sch:pattern>
  <sch:pattern>
    <sch:title>XdsEntry</sch:title>
    <sch:rule context="/a:feed/a:entry/a:content/f:XdsEntry">
      <sch:assert test="exists(f:url) or exists(f:repositoryId) ">A rul or a repositoryId must be provided</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:XdsEntry/f:folder">
      <sch:assert test="not(contains(f:version, ':')) or exists(f:id)">If the version url is provided, and local, then a logical id must be provided too</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:XdsEntry/f:patientInfo">
      <sch:assert test="not(contains(f:version, ':')) or exists(f:id)">If the version url is provided, and local, then a logical id must be provided too</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:XdsEntry/f:author">
      <sch:assert test="exists(f:name) or exists(f:id) or exists(f:contact) or exists(f:institution)">At least a name, id, contact, or institution sub-attribute shall be present</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:XdsEntry/f:author/f:contact">
      <sch:assert test="not(exists(f:value)) or exists(f:system)">A system is required if a value is provided.</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:XdsEntry/f:authenticator">
      <sch:assert test="exists(f:name) or exists(f:id) or exists(f:contact) or exists(f:institution)">A name or id must be provided</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:XdsEntry/f:extension">
      <sch:assert test="not(exists(f:extension/f:ref))">Nexted Extensions cannot have references</sch:assert>
      <sch:assert test="exists(f:*[starts-with(local-name(.), 'value')]) != exists(f:extension)">Either a value or nested extensions must be present, but not both</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:XdsEntry/f:extension//f:extension">
      <sch:assert test="not(exists(f:extension/f:ref))">Nexted Extensions cannot have references</sch:assert>
      <sch:assert test="exists(f:*[starts-with(local-name(.), 'value')]) != exists(f:extension)">Either a value or nested extensions must be present, but not both</sch:assert>
    </sch:rule>
    </sch:pattern>
  <sch:pattern>
    <sch:title>Conformance</sch:title>
    <sch:rule context="/a:feed/a:entry/a:content/f:Conformance">
      <sch:assert test="count(f:software | f:implementation | f:proposal) = 1">Must have one and only one of software, implementation or proposal</sch:assert>
      <sch:assert test="exists(f:rest) or exists(f:messaging) or exists(f:document)">A Conformance profile must have at least one of rest, messaging, or document</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:Conformance/f:publisher/f:contact">
      <sch:assert test="not(exists(f:value)) or exists(f:system)">A system is required if a value is provided.</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:Conformance/f:messaging">
      <sch:assert test="exists(f:endpoint) = exists(parent::f:Conformance/f:implementation)">Messaging end point is required (and is only permitted) when statement is for an implementation</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:Conformance/f:extension">
      <sch:assert test="not(exists(f:extension/f:ref))">Nexted Extensions cannot have references</sch:assert>
      <sch:assert test="exists(f:*[starts-with(local-name(.), 'value')]) != exists(f:extension)">Either a value or nested extensions must be present, but not both</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:Conformance/f:extension//f:extension">
      <sch:assert test="not(exists(f:extension/f:ref))">Nexted Extensions cannot have references</sch:assert>
      <sch:assert test="exists(f:*[starts-with(local-name(.), 'value')]) != exists(f:extension)">Either a value or nested extensions must be present, but not both</sch:assert>
    </sch:rule>
    </sch:pattern>
  <sch:pattern>
    <sch:title>DocumentHeader</sch:title>
    <sch:rule context="/a:feed/a:entry/a:content/f:DocumentHeader">
      <sch:assert test="exists(f:representation) or exists(f:section)">A document must have a representation, one or more sections or both</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:DocumentHeader/f:type">
      <sch:assert test="not(exists(f:primary)) or count(f:coding[@id=current()/f:primary])=1">If a primary reference is present, it must point to one of the codings</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:DocumentHeader/f:enterer">
      <sch:assert test="not(contains(f:version, ':')) or exists(f:id)">If the version url is provided, and local, then a logical id must be provided too</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:DocumentHeader/f:subject">
      <sch:assert test="not(contains(f:version, ':')) or exists(f:id)">If the version url is provided, and local, then a logical id must be provided too</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:DocumentHeader/f:informant">
      <sch:assert test="not(contains(f:version, ':')) or exists(f:id)">If the version url is provided, and local, then a logical id must be provided too</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:DocumentHeader/f:author">
      <sch:assert test="not(contains(f:version, ':')) or exists(f:id)">If the version url is provided, and local, then a logical id must be provided too</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:DocumentHeader/f:attester/f:party">
      <sch:assert test="not(contains(f:version, ':')) or exists(f:id)">If the version url is provided, and local, then a logical id must be provided too</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:DocumentHeader/f:recipient">
      <sch:assert test="not(contains(f:version, ':')) or exists(f:id)">If the version url is provided, and local, then a logical id must be provided too</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:DocumentHeader/f:custodian">
      <sch:assert test="not(contains(f:version, ':')) or exists(f:id)">If the version url is provided, and local, then a logical id must be provided too</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:DocumentHeader/f:context">
      <sch:assert test="not(contains(f:version, ':')) or exists(f:id)">If the version url is provided, and local, then a logical id must be provided too</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:DocumentHeader/f:encounter">
      <sch:assert test="not(contains(f:version, ':')) or exists(f:id)">If the version url is provided, and local, then a logical id must be provided too</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:DocumentHeader/f:stylesheet">
      <sch:assert test="f:mimeType = 'text/css'">A document stylesheet must have a mime type of text/css</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:DocumentHeader/f:section">
      <sch:assert test="exists(f:content) != exists(f:section)">A section must have content or one or more sections, but not both.</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:DocumentHeader/f:section/f:type">
      <sch:assert test="not(exists(f:primary)) or count(f:coding[@id=current()/f:primary])=1">If a primary reference is present, it must point to one of the codings</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:DocumentHeader/f:section/f:author">
      <sch:assert test="not(contains(f:version, ':')) or exists(f:id)">If the version url is provided, and local, then a logical id must be provided too</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:DocumentHeader/f:section/f:enterer">
      <sch:assert test="not(contains(f:version, ':')) or exists(f:id)">If the version url is provided, and local, then a logical id must be provided too</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:DocumentHeader/f:section/f:subject">
      <sch:assert test="not(contains(f:version, ':')) or exists(f:id)">If the version url is provided, and local, then a logical id must be provided too</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:DocumentHeader/f:section/f:informant">
      <sch:assert test="not(contains(f:version, ':')) or exists(f:id)">If the version url is provided, and local, then a logical id must be provided too</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:DocumentHeader/f:section/f:content">
      <sch:assert test="not(contains(f:version, ':')) or exists(f:id)">If the version url is provided, and local, then a logical id must be provided too</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:DocumentHeader/f:extension">
      <sch:assert test="not(exists(f:extension/f:ref))">Nexted Extensions cannot have references</sch:assert>
      <sch:assert test="exists(f:*[starts-with(local-name(.), 'value')]) != exists(f:extension)">Either a value or nested extensions must be present, but not both</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:DocumentHeader/f:extension//f:extension">
      <sch:assert test="not(exists(f:extension/f:ref))">Nexted Extensions cannot have references</sch:assert>
      <sch:assert test="exists(f:*[starts-with(local-name(.), 'value')]) != exists(f:extension)">Either a value or nested extensions must be present, but not both</sch:assert>
    </sch:rule>
    </sch:pattern>
  <sch:pattern>
    <sch:title>Device</sch:title>
    <sch:rule context="/a:feed/a:entry/a:content/f:Device/f:type">
      <sch:assert test="not(exists(f:primary)) or count(f:coding[@id=current()/f:primary])=1">If a primary reference is present, it must point to one of the codings</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:Device/f:owner">
      <sch:assert test="not(contains(f:version, ':')) or exists(f:id)">If the version url is provided, and local, then a logical id must be provided too</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:Device/f:location">
      <sch:assert test="not(contains(f:version, ':')) or exists(f:id)">If the version url is provided, and local, then a logical id must be provided too</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:Device/f:contact">
      <sch:assert test="not(exists(f:value)) or exists(f:system)">A system is required if a value is provided.</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:Device/f:address">
      <sch:assert test="not(exists(f:value)) or exists(f:system)">A system is required if a value is provided.</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:Device/f:extension">
      <sch:assert test="not(exists(f:extension/f:ref))">Nexted Extensions cannot have references</sch:assert>
      <sch:assert test="exists(f:*[starts-with(local-name(.), 'value')]) != exists(f:extension)">Either a value or nested extensions must be present, but not both</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:Device/f:extension//f:extension">
      <sch:assert test="not(exists(f:extension/f:ref))">Nexted Extensions cannot have references</sch:assert>
      <sch:assert test="exists(f:*[starts-with(local-name(.), 'value')]) != exists(f:extension)">Either a value or nested extensions must be present, but not both</sch:assert>
    </sch:rule>
    </sch:pattern>
  <sch:pattern>
    <sch:title>Animal</sch:title>
    <sch:rule context="/a:feed/a:entry/a:content/f:Animal/f:identifier/f:assigner">
      <sch:assert test="not(contains(f:version, ':')) or exists(f:id)">If the version url is provided, and local, then a logical id must be provided too</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:Animal/f:species">
      <sch:assert test="not(exists(f:primary)) or count(f:coding[@id=current()/f:primary])=1">If a primary reference is present, it must point to one of the codings</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:Animal/f:strain">
      <sch:assert test="not(exists(f:primary)) or count(f:coding[@id=current()/f:primary])=1">If a primary reference is present, it must point to one of the codings</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:Animal/f:gender">
      <sch:assert test="not(exists(f:primary)) or count(f:coding[@id=current()/f:primary])=1">If a primary reference is present, it must point to one of the codings</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:Animal/f:relatedEntity/f:identifier/f:assigner">
      <sch:assert test="not(contains(f:version, ':')) or exists(f:id)">If the version url is provided, and local, then a logical id must be provided too</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:Animal/f:relatedEntity/f:role">
      <sch:assert test="not(exists(f:primary)) or count(f:coding[@id=current()/f:primary])=1">If a primary reference is present, it must point to one of the codings</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:Animal/f:relatedEntity/f:contact">
      <sch:assert test="not(exists(f:value)) or exists(f:system)">A system is required if a value is provided.</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:Animal/f:extension">
      <sch:assert test="not(exists(f:extension/f:ref))">Nexted Extensions cannot have references</sch:assert>
      <sch:assert test="exists(f:*[starts-with(local-name(.), 'value')]) != exists(f:extension)">Either a value or nested extensions must be present, but not both</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:Animal/f:extension//f:extension">
      <sch:assert test="not(exists(f:extension/f:ref))">Nexted Extensions cannot have references</sch:assert>
      <sch:assert test="exists(f:*[starts-with(local-name(.), 'value')]) != exists(f:extension)">Either a value or nested extensions must be present, but not both</sch:assert>
    </sch:rule>
    </sch:pattern>
  <sch:pattern>
    <sch:title>Agent</sch:title>
    <sch:rule context="/a:feed/a:entry/a:content/f:Agent/f:person">
      <sch:assert test="not(contains(f:version, ':')) or exists(f:id)">If the version url is provided, and local, then a logical id must be provided too</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:Agent/f:organization">
      <sch:assert test="not(contains(f:version, ':')) or exists(f:id)">If the version url is provided, and local, then a logical id must be provided too</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:Agent/f:role">
      <sch:assert test="not(exists(f:primary)) or count(f:coding[@id=current()/f:primary])=1">If a primary reference is present, it must point to one of the codings</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:Agent/f:identifier/f:assigner">
      <sch:assert test="not(contains(f:version, ':')) or exists(f:id)">If the version url is provided, and local, then a logical id must be provided too</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:Agent/f:contact">
      <sch:assert test="not(exists(f:value)) or exists(f:system)">A system is required if a value is provided.</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:Agent/f:extension">
      <sch:assert test="not(exists(f:extension/f:ref))">Nexted Extensions cannot have references</sch:assert>
      <sch:assert test="exists(f:*[starts-with(local-name(.), 'value')]) != exists(f:extension)">Either a value or nested extensions must be present, but not both</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:Agent/f:extension//f:extension">
      <sch:assert test="not(exists(f:extension/f:ref))">Nexted Extensions cannot have references</sch:assert>
      <sch:assert test="exists(f:*[starts-with(local-name(.), 'value')]) != exists(f:extension)">Either a value or nested extensions must be present, but not both</sch:assert>
    </sch:rule>
    </sch:pattern>
  <sch:pattern>
    <sch:title>Organization</sch:title>
    <sch:rule context="/a:feed/a:entry/a:content/f:Organization/f:identifier/f:assigner">
      <sch:assert test="not(contains(f:version, ':')) or exists(f:id)">If the version url is provided, and local, then a logical id must be provided too</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:Organization/f:contact">
      <sch:assert test="not(exists(f:value)) or exists(f:system)">A system is required if a value is provided.</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:Organization/f:code">
      <sch:assert test="not(exists(f:primary)) or count(f:coding[@id=current()/f:primary])=1">If a primary reference is present, it must point to one of the codings</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:Organization/f:industryCode">
      <sch:assert test="not(exists(f:primary)) or count(f:coding[@id=current()/f:primary])=1">If a primary reference is present, it must point to one of the codings</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:Organization/f:accreditation/f:code">
      <sch:assert test="not(exists(f:primary)) or count(f:coding[@id=current()/f:primary])=1">If a primary reference is present, it must point to one of the codings</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:Organization/f:accreditation/f:institution">
      <sch:assert test="not(contains(f:version, ':')) or exists(f:id)">If the version url is provided, and local, then a logical id must be provided too</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:Organization/f:relatedOrganization/f:identifier/f:assigner">
      <sch:assert test="not(contains(f:version, ':')) or exists(f:id)">If the version url is provided, and local, then a logical id must be provided too</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:Organization/f:relatedOrganization/f:code">
      <sch:assert test="not(exists(f:primary)) or count(f:coding[@id=current()/f:primary])=1">If a primary reference is present, it must point to one of the codings</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:Organization/f:relatedOrganization/f:contact">
      <sch:assert test="not(exists(f:value)) or exists(f:system)">A system is required if a value is provided.</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:Organization/f:extension">
      <sch:assert test="not(exists(f:extension/f:ref))">Nexted Extensions cannot have references</sch:assert>
      <sch:assert test="exists(f:*[starts-with(local-name(.), 'value')]) != exists(f:extension)">Either a value or nested extensions must be present, but not both</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:Organization/f:extension//f:extension">
      <sch:assert test="not(exists(f:extension/f:ref))">Nexted Extensions cannot have references</sch:assert>
      <sch:assert test="exists(f:*[starts-with(local-name(.), 'value')]) != exists(f:extension)">Either a value or nested extensions must be present, but not both</sch:assert>
    </sch:rule>
    </sch:pattern>
  <sch:pattern>
    <sch:title>Prescription</sch:title>
    <sch:rule context="/a:feed/a:entry/a:content/f:Prescription/f:identifier/f:assigner">
      <sch:assert test="not(contains(f:version, ':')) or exists(f:id)">If the version url is provided, and local, then a logical id must be provided too</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:Prescription/f:patient">
      <sch:assert test="not(contains(f:version, ':')) or exists(f:id)">If the version url is provided, and local, then a logical id must be provided too</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:Prescription/f:prescriber">
      <sch:assert test="not(contains(f:version, ':')) or exists(f:id)">If the version url is provided, and local, then a logical id must be provided too</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:Prescription/f:dispense/f:quantity">
      <sch:assert test="not(exists(f:code)) or exists(f:system)">If a code for the units is present, the system must also be present</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:Prescription/f:dispense/f:dispenser">
      <sch:assert test="not(contains(f:version, ':')) or exists(f:id)">If the version url is provided, and local, then a logical id must be provided too</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:Prescription/f:medicine/f:identification">
      <sch:assert test="not(exists(f:primary)) or count(f:coding[@id=current()/f:primary])=1">If a primary reference is present, it must point to one of the codings</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:Prescription/f:medicine/f:activeIngredient/f:identification">
      <sch:assert test="not(exists(f:primary)) or count(f:coding[@id=current()/f:primary])=1">If a primary reference is present, it must point to one of the codings</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:Prescription/f:medicine/f:activeIngredient/f:quantityRatio/f:numerator">
      <sch:assert test="not(exists(f:code)) or exists(f:system)">If a code for the units is present, the system must also be present</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:Prescription/f:medicine/f:activeIngredient/f:quantityRatio/f:denominator">
      <sch:assert test="not(exists(f:code)) or exists(f:system)">If a code for the units is present, the system must also be present</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:Prescription/f:medicine/f:inactiveIngredient/f:identification">
      <sch:assert test="not(exists(f:primary)) or count(f:coding[@id=current()/f:primary])=1">If a primary reference is present, it must point to one of the codings</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:Prescription/f:medicine/f:inactiveIngredient/f:quantityRatio/f:numerator">
      <sch:assert test="not(exists(f:code)) or exists(f:system)">If a code for the units is present, the system must also be present</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:Prescription/f:medicine/f:inactiveIngredient/f:quantityRatio/f:denominator">
      <sch:assert test="not(exists(f:code)) or exists(f:system)">If a code for the units is present, the system must also be present</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:Prescription/f:administrationRequest">
      <sch:assert test="(exists(f:duration) and not (exists(f:start) or exists(f:end))) or (exists(f:start) and exists(f:duration)) != (exists(f:start))">Restrictions on the combinations of Duration, Start and End are as follows: Duration may be specified alone or with Start.  Alternatively, Start may be specified if End is specified.  No other combinations are allowed.</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:Prescription/f:administrationRequest/f:totalPeriodicDosis/f:numerator">
      <sch:assert test="not(exists(f:code)) or exists(f:system)">If a code for the units is present, the system must also be present</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:Prescription/f:administrationRequest/f:totalPeriodicDosis/f:denominator">
      <sch:assert test="not(exists(f:code)) or exists(f:system)">If a code for the units is present, the system must also be present</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:Prescription/f:administrationRequest/f:duration">
      <sch:assert test="not(exists(f:code)) or exists(f:system)">If a code for the units is present, the system must also be present</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:Prescription/f:administrationRequest/f:dosageInstruction/f:precondition">
      <sch:assert test="not(exists(f:primary)) or count(f:coding[@id=current()/f:primary])=1">If a primary reference is present, it must point to one of the codings</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:Prescription/f:administrationRequest/f:dosageInstruction/f:additionalInstruction">
      <sch:assert test="not(exists(f:primary)) or count(f:coding[@id=current()/f:primary])=1">If a primary reference is present, it must point to one of the codings</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:Prescription/f:administrationRequest/f:dosageInstruction/f:route">
      <sch:assert test="not(exists(f:primary)) or count(f:coding[@id=current()/f:primary])=1">If a primary reference is present, it must point to one of the codings</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:Prescription/f:administrationRequest/f:dosageInstruction/f:doseQuantity">
      <sch:assert test="not(exists(f:code)) or exists(f:system)">If a code for the units is present, the system must also be present</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:Prescription/f:administrationRequest/f:dosageInstruction/f:doseRange">
      <sch:assert test="not(exists(f:low/f:range) or exists(f:high/f:range))">Quantity values cannot have a range when used in a Range</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:Prescription/f:administrationRequest/f:dosageInstruction/f:doseRange/f:low">
      <sch:assert test="not(exists(f:code)) or exists(f:system)">If a code for the units is present, the system must also be present</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:Prescription/f:administrationRequest/f:dosageInstruction/f:doseRange/f:high">
      <sch:assert test="not(exists(f:code)) or exists(f:system)">If a code for the units is present, the system must also be present</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:Prescription/f:administrationRequest/f:dosageInstruction/f:rate">
      <sch:assert test="not(exists(f:code)) or exists(f:system)">If a code for the units is present, the system must also be present</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:Prescription/f:administrationRequest/f:dosageInstruction/f:schedule">
      <sch:assert test="not(exists(f:repeat)) or count(f:event)&lt;2">There can only be a repeat element if there is none or one event</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:Prescription/f:administrationRequest/f:dosageInstruction/f:schedule/f:repeat">
      <sch:assert test="not(exists(f:count) and exists(f:end))">At most, only one of count and end can be present</sch:assert>
      <sch:assert test="exists(f:frequency) != exists(f:when)">Either frequency or when must be present, but not both</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:Prescription/f:administrationRequest/f:dosageInstruction/f:schedule/f:repeat/f:duration">
      <sch:assert test=". &gt; 0">duration must be a positive value</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:Prescription/f:reason">
      <sch:assert test="not(exists(f:primary)) or count(f:coding[@id=current()/f:primary])=1">If a primary reference is present, it must point to one of the codings</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:Prescription/f:extension">
      <sch:assert test="not(exists(f:extension/f:ref))">Nexted Extensions cannot have references</sch:assert>
      <sch:assert test="exists(f:*[starts-with(local-name(.), 'value')]) != exists(f:extension)">Either a value or nested extensions must be present, but not both</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:Prescription/f:extension//f:extension">
      <sch:assert test="not(exists(f:extension/f:ref))">Nexted Extensions cannot have references</sch:assert>
      <sch:assert test="exists(f:*[starts-with(local-name(.), 'value')]) != exists(f:extension)">Either a value or nested extensions must be present, but not both</sch:assert>
    </sch:rule>
    </sch:pattern>
  <sch:pattern>
    <sch:title>Profile</sch:title>
    <sch:rule context="/a:feed/a:entry/a:content/f:Profile/f:resource">
      <sch:assert test="exists(f:profile) != exists(f:element)">Provide either a profile reference or constraints on the resource elements (but not both)</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:Profile/f:resource/f:element">
      <sch:assert test="not(exists(f:bundled)) or exists(f:definition/f:type/f:code[starts-with(., 'Resource(')])">Bundled may only be specified if one of the allowed types for the element is a resource</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:Profile/f:resource/f:element/f:definition">
      <sch:assert test="not(exists(f:nameReference) and exists(f:*[starts-with(local-name(.), 'value')]))">Either a name or a fixed value (but not both) is permitted</sch:assert>
      <sch:assert test="not(exists(f:*[starts-with(local-name(.), 'value')])) or (count(f:type)=1 and f:type/f:code[substring(.,1,1)=lower-case(substring(.,1,1))])">Value may only be specified if the type consists of a single repetition that has a type corresponding to one of the primitive data types.</sch:assert>
      <sch:assert test="not(exists(f:binding)) or f:type/f:code=('code','Coding','CodeableConcept')">Binding can only be present for coded elements</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:Profile/f:resource/f:element/f:definition/f:max">
      <sch:assert test=".='*' or (normalize-space(.)!='' and normalize-space(translate(., '0123456789',''))='')">Max must be a number or "*"</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:Profile/f:resource/f:element/f:definition/f:type">
      <sch:assert test="not(exists(f:profile)) or starts-with(f:code, 'Resource(')">Profile is only allowed if code is a resource</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:Profile/f:extensionDefn/f:code">
      <sch:assert test="count(ancestor::f:Profile/f:extensionDefn/f:code[.=current()])=1">Codes must be unique in the context of a profile</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:Profile/f:binding">
      <sch:assert test="(f:type=('reference', 'valueset')) = exists(f:reference)">reference must be present and may only be present if binding type is "reference" or "valueset"</sch:assert>
      <sch:assert test="(f:type='codelist') = exists(f:concept)">concepts must be present and may only be present if binding type is "codelist"</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:Profile/f:binding/f:name">
      <sch:assert test="count(ancestor::f:Profile/f:binding/f:name[.=current()])=1">Binding name must be unique in the context of a profile</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:Profile/f:extension">
      <sch:assert test="not(exists(f:extension/f:ref))">Nexted Extensions cannot have references</sch:assert>
      <sch:assert test="exists(f:*[starts-with(local-name(.), 'value')]) != exists(f:extension)">Either a value or nested extensions must be present, but not both</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:Profile/f:extension//f:extension">
      <sch:assert test="not(exists(f:extension/f:ref))">Nexted Extensions cannot have references</sch:assert>
      <sch:assert test="exists(f:*[starts-with(local-name(.), 'value')]) != exists(f:extension)">Either a value or nested extensions must be present, but not both</sch:assert>
    </sch:rule>
    </sch:pattern>
  <sch:pattern>
    <sch:title>ValueSet</sch:title>
    <sch:rule context="/a:feed/a:entry/a:content/f:ValueSet">
      <sch:assert test="count(f:include)!=1 or exists(f:include) or exists(f:exclude)">A value set with only one import must also have an include and/or an exclude</sch:assert>
      <sch:assert test="exists(f:include) or exists(f:import)">A value set must have an include or an import</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:ValueSet/f:include">
      <sch:assert test="count(f:code|f:filter)!=0">A selection criteria must have at least one code or filter</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:ValueSet/f:extension">
      <sch:assert test="not(exists(f:extension/f:ref))">Nexted Extensions cannot have references</sch:assert>
      <sch:assert test="exists(f:*[starts-with(local-name(.), 'value')]) != exists(f:extension)">Either a value or nested extensions must be present, but not both</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:ValueSet/f:extension//f:extension">
      <sch:assert test="not(exists(f:extension/f:ref))">Nexted Extensions cannot have references</sch:assert>
      <sch:assert test="exists(f:*[starts-with(local-name(.), 'value')]) != exists(f:extension)">Either a value or nested extensions must be present, but not both</sch:assert>
    </sch:rule>
    </sch:pattern>
  <sch:pattern>
    <sch:title>Problem</sch:title>
    <sch:rule context="/a:feed/a:entry/a:content/f:Problem/f:code">
      <sch:assert test="not(exists(f:primary)) or count(f:coding[@id=current()/f:primary])=1">If a primary reference is present, it must point to one of the codings</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:Problem/f:category">
      <sch:assert test="not(exists(f:primary)) or count(f:coding[@id=current()/f:primary])=1">If a primary reference is present, it must point to one of the codings</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:Problem/f:certainty">
      <sch:assert test="not(exists(f:primary)) or count(f:coding[@id=current()/f:primary])=1">If a primary reference is present, it must point to one of the codings</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:Problem/f:severity">
      <sch:assert test="not(exists(f:primary)) or count(f:coding[@id=current()/f:primary])=1">If a primary reference is present, it must point to one of the codings</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:Problem/f:onsetAge">
      <sch:assert test="not(exists(f:code)) or exists(f:system)">If a code for the units is present, the system must also be present</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:Problem/f:abatementAge">
      <sch:assert test="not(exists(f:code)) or exists(f:system)">If a code for the units is present, the system must also be present</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:Problem/f:stage">
      <sch:assert test="exists(f:summary) or exists(f:assessment)">Stage must have summary or assessment</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:Problem/f:stage/f:summary">
      <sch:assert test="not(exists(f:primary)) or count(f:coding[@id=current()/f:primary])=1">If a primary reference is present, it must point to one of the codings</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:Problem/f:stage/f:assessment">
      <sch:assert test="not(contains(f:version, ':')) or exists(f:id)">If the version url is provided, and local, then a logical id must be provided too</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:Problem/f:evidence">
      <sch:assert test="exists(f:code) or exists(f:details)">evidence must have code or details</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:Problem/f:evidence/f:code">
      <sch:assert test="not(exists(f:primary)) or count(f:coding[@id=current()/f:primary])=1">If a primary reference is present, it must point to one of the codings</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:Problem/f:evidence/f:details">
      <sch:assert test="not(contains(f:version, ':')) or exists(f:id)">If the version url is provided, and local, then a logical id must be provided too</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:Problem/f:location">
      <sch:assert test="exists(f:code) or exists(f:details)">location must have code or details</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:Problem/f:location/f:code">
      <sch:assert test="not(exists(f:primary)) or count(f:coding[@id=current()/f:primary])=1">If a primary reference is present, it must point to one of the codings</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:Problem/f:location/f:details">
      <sch:assert test="not(contains(f:version, ':')) or exists(f:id)">If the version url is provided, and local, then a logical id must be provided too</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:Problem/f:relatedItem/f:target">
      <sch:assert test="not(contains(f:version, ':')) or exists(f:id)">If the version url is provided, and local, then a logical id must be provided too</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:Problem/f:extension">
      <sch:assert test="not(exists(f:extension/f:ref))">Nexted Extensions cannot have references</sch:assert>
      <sch:assert test="exists(f:*[starts-with(local-name(.), 'value')]) != exists(f:extension)">Either a value or nested extensions must be present, but not both</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:Problem/f:extension//f:extension">
      <sch:assert test="not(exists(f:extension/f:ref))">Nexted Extensions cannot have references</sch:assert>
      <sch:assert test="exists(f:*[starts-with(local-name(.), 'value')]) != exists(f:extension)">Either a value or nested extensions must be present, but not both</sch:assert>
    </sch:rule>
    </sch:pattern>
  <sch:pattern>
    <sch:title>Test</sch:title>
    <sch:rule context="/a:feed/a:entry/a:content/f:Test/f:extension">
      <sch:assert test="not(exists(f:extension/f:ref))">Nexted Extensions cannot have references</sch:assert>
      <sch:assert test="exists(f:*[starts-with(local-name(.), 'value')]) != exists(f:extension)">Either a value or nested extensions must be present, but not both</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:Test/f:extension//f:extension">
      <sch:assert test="not(exists(f:extension/f:ref))">Nexted Extensions cannot have references</sch:assert>
      <sch:assert test="exists(f:*[starts-with(local-name(.), 'value')]) != exists(f:extension)">Either a value or nested extensions must be present, but not both</sch:assert>
    </sch:rule>
    </sch:pattern>
  <sch:pattern>
    <sch:title>MessageHeader</sch:title>
    <sch:rule context="/a:feed/a:entry/a:content/f:MessageHeader/f:response/f:details">
      <sch:assert test="not(contains(f:version, ':')) or exists(f:id)">If the version url is provided, and local, then a logical id must be provided too</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:MessageHeader/f:source/f:contact">
      <sch:assert test="not(exists(f:value)) or exists(f:system)">A system is required if a value is provided.</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:MessageHeader/f:destination/f:target">
      <sch:assert test="not(contains(f:version, ':')) or exists(f:id)">If the version url is provided, and local, then a logical id must be provided too</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:MessageHeader/f:enterer">
      <sch:assert test="not(contains(f:version, ':')) or exists(f:id)">If the version url is provided, and local, then a logical id must be provided too</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:MessageHeader/f:author">
      <sch:assert test="not(contains(f:version, ':')) or exists(f:id)">If the version url is provided, and local, then a logical id must be provided too</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:MessageHeader/f:receiver">
      <sch:assert test="not(contains(f:version, ':')) or exists(f:id)">If the version url is provided, and local, then a logical id must be provided too</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:MessageHeader/f:responsible">
      <sch:assert test="not(contains(f:version, ':')) or exists(f:id)">If the version url is provided, and local, then a logical id must be provided too</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:MessageHeader/f:reason">
      <sch:assert test="not(exists(f:primary)) or count(f:coding[@id=current()/f:primary])=1">If a primary reference is present, it must point to one of the codings</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:MessageHeader/f:data">
      <sch:assert test="not(contains(f:version, ':')) or exists(f:id)">If the version url is provided, and local, then a logical id must be provided too</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:MessageHeader/f:extension">
      <sch:assert test="not(exists(f:extension/f:ref))">Nexted Extensions cannot have references</sch:assert>
      <sch:assert test="exists(f:*[starts-with(local-name(.), 'value')]) != exists(f:extension)">Either a value or nested extensions must be present, but not both</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:MessageHeader/f:extension//f:extension">
      <sch:assert test="not(exists(f:extension/f:ref))">Nexted Extensions cannot have references</sch:assert>
      <sch:assert test="exists(f:*[starts-with(local-name(.), 'value')]) != exists(f:extension)">Either a value or nested extensions must be present, but not both</sch:assert>
    </sch:rule>
    </sch:pattern>
  <sch:pattern>
    <sch:title>SecurityEvent</sch:title>
    <sch:rule context="/a:feed/a:entry/a:content/f:SecurityEvent/f:extension">
      <sch:assert test="not(exists(f:extension/f:ref))">Nexted Extensions cannot have references</sch:assert>
      <sch:assert test="exists(f:*[starts-with(local-name(.), 'value')]) != exists(f:extension)">Either a value or nested extensions must be present, but not both</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:SecurityEvent/f:extension//f:extension">
      <sch:assert test="not(exists(f:extension/f:ref))">Nexted Extensions cannot have references</sch:assert>
      <sch:assert test="exists(f:*[starts-with(local-name(.), 'value')]) != exists(f:extension)">Either a value or nested extensions must be present, but not both</sch:assert>
    </sch:rule>
    </sch:pattern>
  <sch:pattern>
    <sch:title>AssessmentScale</sch:title>
    <sch:rule context="/a:feed/a:entry/a:content/f:AssessmentScale/f:subject">
      <sch:assert test="not(contains(f:version, ':')) or exists(f:id)">If the version url is provided, and local, then a logical id must be provided too</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:AssessmentScale/f:performer">
      <sch:assert test="not(contains(f:version, ':')) or exists(f:id)">If the version url is provided, and local, then a logical id must be provided too</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:AssessmentScale/f:definition">
      <sch:assert test="not(contains(f:version, ':')) or exists(f:id)">If the version url is provided, and local, then a logical id must be provided too</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:AssessmentScale/f:interpretation">
      <sch:assert test="not(exists(f:primary)) or count(f:coding[@id=current()/f:primary])=1">If a primary reference is present, it must point to one of the codings</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:AssessmentScale/f:score">
      <sch:assert test="(exists(f:*[starts-with(local-name(.), 'value')]) or exists(f:measure) != exists(f:score)) and not (exists(f:measure) and exists(f:score))">A score may have a value and/or one (and only one) of measure or sub-scores</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:AssessmentScale/f:score/f:code">
      <sch:assert test="not(exists(f:primary)) or count(f:coding[@id=current()/f:primary])=1">If a primary reference is present, it must point to one of the codings</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:AssessmentScale/f:score/f:valueQuantity">
      <sch:assert test="not(exists(f:code)) or exists(f:system)">If a code for the units is present, the system must also be present</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:AssessmentScale/f:score/f:valueCodeableConcept">
      <sch:assert test="not(exists(f:primary)) or count(f:coding[@id=current()/f:primary])=1">If a primary reference is present, it must point to one of the codings</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:AssessmentScale/f:score/f:measure/f:code">
      <sch:assert test="not(exists(f:primary)) or count(f:coding[@id=current()/f:primary])=1">If a primary reference is present, it must point to one of the codings</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:AssessmentScale/f:score/f:measure/f:valueQuantity">
      <sch:assert test="not(exists(f:code)) or exists(f:system)">If a code for the units is present, the system must also be present</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:AssessmentScale/f:score/f:measure/f:valueCodeableConcept">
      <sch:assert test="not(exists(f:primary)) or count(f:coding[@id=current()/f:primary])=1">If a primary reference is present, it must point to one of the codings</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:AssessmentScale/f:score/f:measure/f:source">
      <sch:assert test="not(contains(f:version, ':')) or exists(f:id)">If the version url is provided, and local, then a logical id must be provided too</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:AssessmentScale/f:extension">
      <sch:assert test="not(exists(f:extension/f:ref))">Nexted Extensions cannot have references</sch:assert>
      <sch:assert test="exists(f:*[starts-with(local-name(.), 'value')]) != exists(f:extension)">Either a value or nested extensions must be present, but not both</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:AssessmentScale/f:extension//f:extension">
      <sch:assert test="not(exists(f:extension/f:ref))">Nexted Extensions cannot have references</sch:assert>
      <sch:assert test="exists(f:*[starts-with(local-name(.), 'value')]) != exists(f:extension)">Either a value or nested extensions must be present, but not both</sch:assert>
    </sch:rule>
    </sch:pattern>
  <sch:pattern>
    <sch:title>Patient</sch:title>
    <sch:rule context="/a:feed/a:entry/a:content/f:Patient/f:link">
      <sch:assert test="not(contains(f:version, ':')) or exists(f:id)">If the version url is provided, and local, then a logical id must be provided too</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:Patient/f:subject">
      <sch:assert test="not(contains(f:version, ':')) or exists(f:id)">If the version url is provided, and local, then a logical id must be provided too</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:Patient/f:provider">
      <sch:assert test="not(contains(f:version, ':')) or exists(f:id)">If the version url is provided, and local, then a logical id must be provided too</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:Patient/f:identifier/f:assigner">
      <sch:assert test="not(contains(f:version, ':')) or exists(f:id)">If the version url is provided, and local, then a logical id must be provided too</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:Patient/f:diet">
      <sch:assert test="not(exists(f:primary)) or count(f:coding[@id=current()/f:primary])=1">If a primary reference is present, it must point to one of the codings</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:Patient/f:confidentiality">
      <sch:assert test="not(exists(f:primary)) or count(f:coding[@id=current()/f:primary])=1">If a primary reference is present, it must point to one of the codings</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:Patient/f:recordLocation">
      <sch:assert test="not(exists(f:primary)) or count(f:coding[@id=current()/f:primary])=1">If a primary reference is present, it must point to one of the codings</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:Patient/f:extension">
      <sch:assert test="not(exists(f:extension/f:ref))">Nexted Extensions cannot have references</sch:assert>
      <sch:assert test="exists(f:*[starts-with(local-name(.), 'value')]) != exists(f:extension)">Either a value or nested extensions must be present, but not both</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:Patient/f:extension//f:extension">
      <sch:assert test="not(exists(f:extension/f:ref))">Nexted Extensions cannot have references</sch:assert>
      <sch:assert test="exists(f:*[starts-with(local-name(.), 'value')]) != exists(f:extension)">Either a value or nested extensions must be present, but not both</sch:assert>
    </sch:rule>
    </sch:pattern>
  <sch:pattern>
    <sch:title>IssueReport</sch:title>
    <sch:rule context="/a:feed/a:entry/a:content/f:IssueReport/f:issue/f:type">
      <sch:assert test="not(exists(f:primary)) or count(f:coding[@id=current()/f:primary])=1">If a primary reference is present, it must point to one of the codings</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:IssueReport/f:extension">
      <sch:assert test="not(exists(f:extension/f:ref))">Nexted Extensions cannot have references</sch:assert>
      <sch:assert test="exists(f:*[starts-with(local-name(.), 'value')]) != exists(f:extension)">Either a value or nested extensions must be present, but not both</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:IssueReport/f:extension//f:extension">
      <sch:assert test="not(exists(f:extension/f:ref))">Nexted Extensions cannot have references</sch:assert>
      <sch:assert test="exists(f:*[starts-with(local-name(.), 'value')]) != exists(f:extension)">Either a value or nested extensions must be present, but not both</sch:assert>
    </sch:rule>
    </sch:pattern>
  <sch:pattern>
    <sch:title>Person</sch:title>
    <sch:rule context="/a:feed/a:entry/a:content/f:Person/f:identifier/f:assigner">
      <sch:assert test="not(contains(f:version, ':')) or exists(f:id)">If the version url is provided, and local, then a logical id must be provided too</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:Person/f:telecom">
      <sch:assert test="not(exists(f:value)) or exists(f:system)">A system is required if a value is provided.</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:Person/f:gender">
      <sch:assert test="not(exists(f:primary)) or count(f:coding[@id=current()/f:primary])=1">If a primary reference is present, it must point to one of the codings</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:Person/f:maritalStatus">
      <sch:assert test="not(exists(f:primary)) or count(f:coding[@id=current()/f:primary])=1">If a primary reference is present, it must point to one of the codings</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:Person/f:language/f:languageCode">
      <sch:assert test="not(exists(f:primary)) or count(f:coding[@id=current()/f:primary])=1">If a primary reference is present, it must point to one of the codings</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:Person/f:language/f:modeCode">
      <sch:assert test="not(exists(f:primary)) or count(f:coding[@id=current()/f:primary])=1">If a primary reference is present, it must point to one of the codings</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:Person/f:language/f:proficiencyLevelCode">
      <sch:assert test="not(exists(f:primary)) or count(f:coding[@id=current()/f:primary])=1">If a primary reference is present, it must point to one of the codings</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:Person/f:extension">
      <sch:assert test="not(exists(f:extension/f:ref))">Nexted Extensions cannot have references</sch:assert>
      <sch:assert test="exists(f:*[starts-with(local-name(.), 'value')]) != exists(f:extension)">Either a value or nested extensions must be present, but not both</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:Person/f:extension//f:extension">
      <sch:assert test="not(exists(f:extension/f:ref))">Nexted Extensions cannot have references</sch:assert>
      <sch:assert test="exists(f:*[starts-with(local-name(.), 'value')]) != exists(f:extension)">Either a value or nested extensions must be present, but not both</sch:assert>
    </sch:rule>
    </sch:pattern>
  <sch:pattern>
    <sch:title>LabReport</sch:title>
    <sch:rule context="/a:feed/a:entry/a:content/f:LabReport/f:patient">
      <sch:assert test="not(contains(f:version, ':')) or exists(f:id)">If the version url is provided, and local, then a logical id must be provided too</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:LabReport/f:admission">
      <sch:assert test="not(contains(f:version, ':')) or exists(f:id)">If the version url is provided, and local, then a logical id must be provided too</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:LabReport/f:laboratory">
      <sch:assert test="not(contains(f:version, ':')) or exists(f:id)">If the version url is provided, and local, then a logical id must be provided too</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:LabReport/f:requestDetail/f:requestTest">
      <sch:assert test="not(exists(f:primary)) or count(f:coding[@id=current()/f:primary])=1">If a primary reference is present, it must point to one of the codings</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:LabReport/f:requestDetail/f:requester">
      <sch:assert test="not(contains(f:version, ':')) or exists(f:id)">If the version url is provided, and local, then a logical id must be provided too</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:LabReport/f:requestDetail/f:clinicalInfo">
      <sch:assert test="not(contains(f:version, ':')) or exists(f:id)">If the version url is provided, and local, then a logical id must be provided too</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:LabReport/f:reportName">
      <sch:assert test="not(exists(f:primary)) or count(f:coding[@id=current()/f:primary])=1">If a primary reference is present, it must point to one of the codings</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:LabReport/f:service">
      <sch:assert test="not(exists(f:primary)) or count(f:coding[@id=current()/f:primary])=1">If a primary reference is present, it must point to one of the codings</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:LabReport/f:specimen">
      <sch:assert test="not(contains(f:version, ':')) or exists(f:id)">If the version url is provided, and local, then a logical id must be provided too</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:LabReport/f:resultGroup/f:name">
      <sch:assert test="not(exists(f:primary)) or count(f:coding[@id=current()/f:primary])=1">If a primary reference is present, it must point to one of the codings</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:LabReport/f:resultGroup/f:specimen">
      <sch:assert test="not(contains(f:version, ':')) or exists(f:id)">If the version url is provided, and local, then a logical id must be provided too</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:LabReport/f:resultGroup/f:result/f:name">
      <sch:assert test="not(exists(f:primary)) or count(f:coding[@id=current()/f:primary])=1">If a primary reference is present, it must point to one of the codings</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:LabReport/f:resultGroup/f:result/f:valueQuantity">
      <sch:assert test="not(exists(f:code)) or exists(f:system)">If a code for the units is present, the system must also be present</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:LabReport/f:resultGroup/f:result/f:valueCodeableConcept">
      <sch:assert test="not(exists(f:primary)) or count(f:coding[@id=current()/f:primary])=1">If a primary reference is present, it must point to one of the codings</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:LabReport/f:resultGroup/f:result/f:valueRatio/f:numerator">
      <sch:assert test="not(exists(f:code)) or exists(f:system)">If a code for the units is present, the system must also be present</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:LabReport/f:resultGroup/f:result/f:valueRatio/f:denominator">
      <sch:assert test="not(exists(f:code)) or exists(f:system)">If a code for the units is present, the system must also be present</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:LabReport/f:resultGroup/f:result/f:referenceRange/f:meaning">
      <sch:assert test="not(exists(f:primary)) or count(f:coding[@id=current()/f:primary])=1">If a primary reference is present, it must point to one of the codings</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:LabReport/f:resultGroup/f:result/f:referenceRange/f:rangeQuantity">
      <sch:assert test="not(exists(f:code)) or exists(f:system)">If a code for the units is present, the system must also be present</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:LabReport/f:resultGroup/f:result/f:referenceRange/f:rangeRange">
      <sch:assert test="not(exists(f:low/f:range) or exists(f:high/f:range))">Quantity values cannot have a range when used in a Range</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:LabReport/f:resultGroup/f:result/f:referenceRange/f:rangeRange/f:low">
      <sch:assert test="not(exists(f:code)) or exists(f:system)">If a code for the units is present, the system must also be present</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:LabReport/f:resultGroup/f:result/f:referenceRange/f:rangeRange/f:high">
      <sch:assert test="not(exists(f:code)) or exists(f:system)">If a code for the units is present, the system must also be present</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:LabReport/f:codedDiagnosis">
      <sch:assert test="not(exists(f:primary)) or count(f:coding[@id=current()/f:primary])=1">If a primary reference is present, it must point to one of the codings</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:LabReport/f:extension">
      <sch:assert test="not(exists(f:extension/f:ref))">Nexted Extensions cannot have references</sch:assert>
      <sch:assert test="exists(f:*[starts-with(local-name(.), 'value')]) != exists(f:extension)">Either a value or nested extensions must be present, but not both</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:LabReport/f:extension//f:extension">
      <sch:assert test="not(exists(f:extension/f:ref))">Nexted Extensions cannot have references</sch:assert>
      <sch:assert test="exists(f:*[starts-with(local-name(.), 'value')]) != exists(f:extension)">Either a value or nested extensions must be present, but not both</sch:assert>
    </sch:rule>
    </sch:pattern>
  <sch:pattern>
    <sch:title>XdsFolder</sch:title>
    <sch:rule context="/a:feed/a:entry/a:content/f:XdsFolder/f:extension">
      <sch:assert test="not(exists(f:extension/f:ref))">Nexted Extensions cannot have references</sch:assert>
      <sch:assert test="exists(f:*[starts-with(local-name(.), 'value')]) != exists(f:extension)">Either a value or nested extensions must be present, but not both</sch:assert>
    </sch:rule>
    <sch:rule context="/a:feed/a:entry/a:content/f:XdsFolder/f:extension//f:extension">
      <sch:assert test="not(exists(f:extension/f:ref))">Nexted Extensions cannot have references</sch:assert>
      <sch:assert test="exists(f:*[starts-with(local-name(.), 'value')]) != exists(f:extension)">Either a value or nested extensions must be present, but not both</sch:assert>
    </sch:rule>
    </sch:pattern>
</sch:schema>
