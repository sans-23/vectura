# Vectura: Resilient Logistics & Routing Engine

**Vectura** (Latin for *transportation*) is a high-performance graph computing engine designed for complex supply chain logistics. Unlike standard routing tools, Vectura focuses on **System Resilience**—optimizing routes under dynamic constraints like road closures, network partitions, and traffic spikes.

It is built as a modular system containing the core algorithmic library (`vectura-core`) and a reference REST API application (`vectura-server`).

## 🚀 Key Features

* **Real-World Topology:** Ingests raw **OpenStreetMap (OSM)** data to build "Helicopter-proof" granular road networks using custom ETL pipelines.
* **Smart Routing:** Implements **A* (A-Star)** search with custom heuristics (Haversine + Tortuosity) for optimal pathfinding.
* **Disaster Simulation:** Uses **Disjoint Set Union (DSU)** with path compression to detect network partitions (e.g., floods/roadblocks) in $O(\alpha(N))$ time.
* **High-Frequency Auditing:** Features a streaming **Knuth-Morris-Pratt (KMP)** engine to scan incoming manifest logs for recall patterns without database overhead.

## 🛠️ Tech Stack

* **Language:** Java 17+ (Records, Sealed Classes)
* **Build System:** Maven Multi-Module
* **Data Source:** OpenStreetMap (Overpass API)
* **Core Algorithms:** Graph Theory (Adjacency Lists), DSU, KMP, Priority Queues.

## 📂 Project Structure

This is a multi-module Maven project:

| Module | Description |
| :--- | :--- |
| **`vectura-core`** | The pure Java library containing the Graph Engine, A* Logic, and DSU state manager. No framework dependencies. |
| **`vectura-server`** | A lightweight Spring Boot application that wraps the core engine to expose REST APIs for simulation. |

## ⚡ Getting Started

### Prerequisites
* Java 17+
* Maven 3.8+

### Installation

1.  Clone the repository:
    ```bash
    git clone [https://github.com/sans-23/vectura.git](https://github.com/sans-23/vectura.git)
    ```
2.  Build the engine:
    ```bash
    mvn clean install
    ```
3.  Run the simulation (example):
    ```bash
    java -jar vectura-server/target/vectura-server-1.0.jar
    ```

## 📈 Algorithmic Deep Dive

### 1. The Routing Engine (A*)
Standard Dijkstra scans too many unnecessary nodes. Vectura uses A* with a specialized heuristic:
$$ f(n) = g(n) + h(n) $$
Where $g(n)$ is the real road distance (OSM data) and $h(n)$ is the Haversine distance to the target.

### 2. The Resilience Engine (DSU)
To handle "Flash Flood" scenarios where 50+ roads close instantly, Vectura uses DSU to track connected components. This allows for $O(1)$ connectivity checks between Warehouses and Customers, avoiding expensive BFS re-calculations.
