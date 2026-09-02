CXX ?= g++
CXXFLAGS ?= -std=c++17 -Wall -Wextra

PY_INCLUDES := $(shell python3-config --includes)
PY_LDFLAGS := $(shell python3-config --embed --ldflags)
NUMPY_INCLUDE := $(shell python3 -c 'import numpy; print(numpy.get_include())')
PLOT_FLAGS := -D__HAS_MATPLOTLIB__ $(PY_INCLUDES) -I$(NUMPY_INCLUDE)

.PHONY: all nogui

all: compmath5 compmath6 compmath6_tests

compmath5: compmath5.cpp matplotlibcpp.h
	$(CXX) $(CXXFLAGS) $(PLOT_FLAGS) compmath5.cpp -o compmath5 $(PY_LDFLAGS)

compmath6: compmath6.cpp matplotlibcpp.h
	$(CXX) $(CXXFLAGS) $(PLOT_FLAGS) compmath6.cpp -o compmath6 $(PY_LDFLAGS)

compmath6_tests: compmath6_tests.cpp
	$(CXX) $(CXXFLAGS) compmath6_tests.cpp -o compmath6_tests

nogui:
	$(CXX) $(CXXFLAGS) compmath5.cpp -o compmath5
	$(CXX) $(CXXFLAGS) compmath6.cpp -o compmath6
	$(CXX) $(CXXFLAGS) compmath6_tests.cpp -o compmath6_tests
