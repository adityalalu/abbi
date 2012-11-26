<?xml version="1.0" encoding="UTF-8"?>
<sch:schema xmlns:sch="http://purl.oclc.org/dsdl/schematron">
  <sch:ns prefix="f" uri="http://hl7.org/fhir"/>
  <sch:pattern>
    <sch:title>MessageHeader</sch:title>
    <sch:rule context="/f:MessageHeader/f:response/f:details">
      <sch:assert test="not(contains(f:version, ':')) or exists(f:id)">If the version url is provided, and local, then a logical id must be provided too</sch:assert>
    </sch:rule>
    <sch:rule context="/f:MessageHeader/f:source/f:contact">
      <sch:assert test="not(exists(f:value)) or exists(f:system)">A system is required if a value is provided.</sch:assert>
    </sch:rule>
    <sch:rule context="/f:MessageHeader/f:destination/f:target">
      <sch:assert test="not(contains(f:version, ':')) or exists(f:id)">If the version url is provided, and local, then a logical id must be provided too</sch:assert>
    </sch:rule>
    <sch:rule context="/f:MessageHeader/f:enterer">
      <sch:assert test="not(contains(f:version, ':')) or exists(f:id)">If the version url is provided, and local, then a logical id must be provided too</sch:assert>
    </sch:rule>
    <sch:rule context="/f:MessageHeader/f:author">
      <sch:assert test="not(contains(f:version, ':')) or exists(f:id)">If the version url is provided, and local, then a logical id must be provided too</sch:assert>
    </sch:rule>
    <sch:rule context="/f:MessageHeader/f:receiver">
      <sch:assert test="not(contains(f:version, ':')) or exists(f:id)">If the version url is provided, and local, then a logical id must be provided too</sch:assert>
    </sch:rule>
    <sch:rule context="/f:MessageHeader/f:responsible">
      <sch:assert test="not(contains(f:version, ':')) or exists(f:id)">If the version url is provided, and local, then a logical id must be provided too</sch:assert>
    </sch:rule>
    <sch:rule context="/f:MessageHeader/f:reason">
      <sch:assert test="not(exists(f:primary)) or count(f:coding[@id=current()/f:primary])=1">If a primary reference is present, it must point to one of the codings</sch:assert>
    </sch:rule>
    <sch:rule context="/f:MessageHeader/f:data">
      <sch:assert test="not(contains(f:version, ':')) or exists(f:id)">If the version url is provided, and local, then a logical id must be provided too</sch:assert>
    </sch:rule>
    <sch:rule context="/f:MessageHeader/f:extension">
      <sch:assert test="not(exists(f:extension/f:ref))">Nexted Extensions cannot have references</sch:assert>
      <sch:assert test="exists(f:*[starts-with(local-name(.), 'value')]) != exists(f:extension)">Either a value or nested extensions must be present, but not both</sch:assert>
    </sch:rule>
    <sch:rule context="/f:MessageHeader/f:extension//f:extension">
      <sch:assert test="not(exists(f:extension/f:ref))">Nexted Extensions cannot have references</sch:assert>
      <sch:assert test="exists(f:*[starts-with(local-name(.), 'value')]) != exists(f:extension)">Either a value or nested extensions must be present, but not both</sch:assert>
    </sch:rule>
  </sch:pattern>
</sch:schema>
