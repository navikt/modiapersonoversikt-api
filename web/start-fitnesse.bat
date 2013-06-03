@echo off
title Dokumentinnsending FitNesse Server
setlocal

%~d0
cd %~p0

mvn test-compile exec:exec -Pfitnesse
