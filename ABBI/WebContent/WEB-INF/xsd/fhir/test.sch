<?xml version="1.0" encoding="UTF-8"?>
<sch:schema xmlns:sch="http://purl.oclc.org/dsdl/schematron">
  <sch:ns prefix="f" uri="http://hl7.org/fhir"/>
  <sch:pattern>
    <sch:title>Test</sch:title>
    <sch:rule context="/f:Test/f:extension">
      <sch:assert test="not(exists(f:extension/f:ref))">Nexted Extensions cannot have references</sch:assert>
      <sch:assert test="exists(f:*[starts-with(local-name(.), 'value')]) != exists(f:extension)">Either a value or nested extensions must be present, but not both</sch:assert>
    </sch:rule>
    <sch:rule context="/f:Test/f:extension//f:extension">
      <sch:assert test="not(exists(f:extension/f:ref))">Nexted Extensions cannot have references</sch:assert>
      <sch:assert test="exists(f:*[starts-with(local-name(.), 'value')]) != exists(f:extension)">Either a value or nested extensions must be present, but not both</sch:assert>
    </sch:rule>
  </sch:pattern>
</sch:schema>
