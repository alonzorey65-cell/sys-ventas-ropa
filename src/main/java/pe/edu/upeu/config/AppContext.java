package pe.edu.upeu.config;

import pe.edu.upeu.controller.*;
import pe.edu.upeu.repository.*;
import pe.edu.upeu.service.*;
import pe.edu.upeu.service.impl.*;
import pe.edu.upeu.utils.ConsultaDNI;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

/*
 * ========================= POO y PATRONES =========================
 * CLASE: AppContext centraliza la creación y entrega de componentes del sistema.
 * PATRÓN SINGLETON (aplicado aquí): una sola instancia global de AppContext.
 *   - `private static AppContext instance;`
 *   - constructor `private AppContext()` evita instanciación externa
 *   - `getInstance()` sincronizado devuelve siempre la misma instancia.
 * PATRÓN IoC/DI simple (contenedor de beans): registra y entrega objetos por tipo.
 * OBJETOS: crea instancias de repositorios, servicios y controladores con "new ...".
 * ENCAPSULAMIENTO: el contenedor Map es private y el acceso se controla por métodos.
 * ABSTRACCIÓN/POLIMORFISMO: resuelve por interfaces (ej. IProductoService), desacoplando capas.
 * CONEXIÓN (inyección/composición): Repository (BD) -> Service (lógica) -> Controller (UI).
 */
public class AppContext { // SINGLETON + contenedor IoC de la aplicación

    private static AppContext instance;

    public static synchronized AppContext getInstance() {
        if (instance == null) {
            instance = new AppContext();
        }
        return instance;
    }

    private final Map<Class<?>, Object> contenedor = new HashMap<>();

    private AppContext() {
        registrarRepositorios();
        registrarServicios();
        registrarControladores();
    }

    private void registrarRepositorios() {
        registrar(CategoriaRepository.class,     new CategoriaRepository());
        registrar(MarcaRepository.class,         new MarcaRepository());
        registrar(UnidadMedidaRepository.class,  new UnidadMedidaRepository());
        registrar(ProductoRepository.class,      new ProductoRepository());
        registrar(UsuarioRepository.class,       new UsuarioRepository());
        registrar(ClienteRepository.class,       new ClienteRepository());
        registrar(VentCarritoRepository.class,   new VentCarritoRepository());
        registrar(VentaDetalleRepository.class,  new VentaDetalleRepository());
        registrar(VentaRepository.class,         new VentaRepository());
    }

    private void registrarServicios() {
        registrar(ConsultaDNI.class, new ConsultaDNI());
        registrar(IMenuMenuItemDao.class, new MenuMenuItemDaoImp());

        registrar(ICategoriaService.class,    new CategoriaServiceImp(    getBean(CategoriaRepository.class)));
        registrar(IMarcaService.class,        new MarcaServiceImp(        getBean(MarcaRepository.class)));
        registrar(IProductoService.class,     new ProductoServiceImp(     getBean(ProductoRepository.class)));
        registrar(IUnidadMedidaService.class, new UnidadMedidaServiceImp( getBean(UnidadMedidaRepository.class)));

        registrar(IUsuarioService.class,      new UsuarioServiceImp(      getBean(UsuarioRepository.class)));
        registrar(IClienteService.class,      new ClienteServiceImp(      getBean(ClienteRepository.class)));

        registrar(IVentaCarritoService.class, new VentaCarritoServiceImp( getBean(VentCarritoRepository.class)));
        registrar(IVentaDetalleService.class, new VentaDetalleServiceImp( getBean(VentaDetalleRepository.class)));
        registrar(IVentaService.class,        new VentaServiceImp(
                getBean(VentaRepository.class),
                getBean(VentaDetalleRepository.class),
                getBean(ProductoRepository.class)));
    }

    private void registrarControladores() {
        registrar(MainGuiController.class, new MainGuiController(getBean(IMenuMenuItemDao.class)));
        registrar(LoginController.class,   new LoginController(getBean(IUsuarioService.class)));
        registrar(ProductoController.class, new ProductoController(
                getBean(IProductoService.class),
                getBean(ICategoriaService.class),
                getBean(IMarcaService.class),
                getBean(IUnidadMedidaService.class)));
        registrar(CategoriaController.class,   new CategoriaController(getBean(ICategoriaService.class)));
        registrar(MarcaController.class,       new MarcaController(getBean(IMarcaService.class)));
        registrar(UnidadMedidaController.class,new UnidadMedidaController(getBean(IUnidadMedidaService.class)));
        registrar(ClienteController.class,     new ClienteController(getBean(IClienteService.class)));
        registrar(UsuarioController.class,     new UsuarioController(getBean(IUsuarioService.class)));
        registrar(VendedorController.class,    new VendedorController(getBean(IVentaService.class)));
        registrar(VentaController.class,   new VentaController(
                getBean(IProductoService.class),
                getBean(IClienteService.class),
                getBean(IVentaService.class),
                getBean(IVentaDetalleService.class)));
        registrar(MainComprobanteController.class, new MainComprobanteController(
                getBean(IVentaService.class),
                getBean(IVentaDetalleService.class),
                getBean(IClienteService.class),
                getBean(IProductoService.class)));
    }

    private void registrar(Class<?> tipo, Object bean) {
        contenedor.put(tipo, bean);
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> tipo) {
        Object bean = contenedor.get(tipo);

        if (bean == null) {
            // Búsqueda por asignación (interfaces o clases hijas)
            bean = contenedor.values().stream()
                    .filter(b -> tipo.isAssignableFrom(b.getClass()))
                    .findFirst()
                    .orElse(null);
        }

        // Si aún es null, aplicamos inyección automática inteligente para controladores
        if (bean == null) {
            try {
                Constructor<?>[] constructors = tipo.getConstructors();
                for (Constructor<?> constructor : constructors) {
                    Class<?>[] paramTypes = constructor.getParameterTypes();
                    if (paramTypes.length == 1 && tipo.getName().contains("controller")) {
                        // Inyecta automáticamente el servicio requerido (ej. IUsuarioService)
                        Object dependency = getBean(paramTypes[0]);
                        bean = constructor.newInstance(dependency);
                        contenedor.put(tipo, bean);
                        return (T) bean;
                    }
                }

                // Fallback final: constructor vacío si no requiere dependencias
                T newInstance = tipo.getDeclaredConstructor().newInstance();
                contenedor.put(tipo, newInstance);
                return newInstance;

            } catch (Exception e) {
                throw new RuntimeException(
                        "Bean no encontrado e imosible de instanciar: " + tipo.getName() +
                                "\n→ Asegúrate de registrarlo o que tenga un constructor válido.", e);
            }
        }

        return (T) bean;
    }
}