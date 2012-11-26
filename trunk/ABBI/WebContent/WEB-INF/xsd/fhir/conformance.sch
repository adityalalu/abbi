<?xml version="1.0" encoding="UTF-8"?>
<sch:schema xmlns:sch="http://purl.oclc.org/dsdl/schematron">
  <sch:ns prefix="f" uri="http://hl7.org/fhir"/>
  <sch:pattern>
    <sch:title>Conformance</sch:title>
    <sch:rule context="/f:Conformance">
      <sch:assert test="count(f:software | f:implementation | f:proposal) = 1">Must have one and only one of software, implementation or proposal</sch:assert>
      <sch:assert test="exists(f:rest) or exists(f:messaging) or exists(f:document)">A Conformance profile must have at least one of rest, messaging, or document</sch:assert>
    </sch:rule>
    <sch:rule context="/f:Conformance/f:publisher/f:contact">
      <sch:assert test="not(exists(f:value)) or exists(f:system)">A system is required if a value is provided.</sch:assert>
    </sch:rule>
    <sch:rule context="/f:Conformance/f:messaging">
      <sch:assert test="exists(f:endpoint) = exists(parent::f:Conformance/f:implementation)">Messaging end point is required (and is only permitted) when statement is for an implementation</sch:assert>
    </sch:rule>
    <sch:rule context="/f:Conformance/f:extension">
      <sch:assert test="not(exists(f:extension/f:ref))">Nexted Extensions cannot have references</sch:assert>
      <sch:assert test="exists(f:*[starts-with(local-name(.), 'value')]) != exists(f:extension)">Either a value or nested extensions must be present, but not both</sch:assert>
    </sch:rule>
    <sch:rule context="/f:Conformance/f:extension//f:extension">
      <sch:assert test="not(exists(f:extension/f:ref))">Nexted Extensions cannot have references</sch:assert>
      <sch:assert test="exists(f:*[starts-with(local-name(.), 'value')]) != exists(f:extension)">Either a value or nested extensions must be present, but not both</sch:assert>
    </sch:rule>
  </sch:pattern>
</sch:schema>
