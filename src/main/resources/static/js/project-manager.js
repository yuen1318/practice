const toLower = text => {
   return text.toString().toLowerCase()
}

const searchByName = (items, term) => {
   if (term) {
     return items.filter(item => toLower(item.name).includes(toLower(term)))
   }

   return items
}

 Vue.use(VueMaterial.default);

  new Vue({
    el: "#app",
    data: {
      selected: {},
      count: 0,
      user: null,
      errorDialog: false,
      errorMessage: null,
      showSidepanel: false,
      showNavigation: false,
      menuVisible: false,
      expandNews: false,
      addDialog: false,
      projectName: null,
      search: null,
      searched: [],
      projects: [
        {
          id: 1,
          name: 'dictionary',
          dateModified: 'Today',
          size: '15mb'
         },
         {
          id: 2,
          name: 'calculator',
          dateModified: 'Today',
          size: '5mb'
        }
      ]
      },

    methods: {
      register() {
        var data = {
          firstName: this.firstName,
          lastName: this.lastName,
          email: this.email,
          password: this.password
        };

        axios.post('/api/v1/auth/register', data)
          .then((response) => {
            console.log(response);
          })
          .catch((error) => {
            this.errorMessage = error.response.data.message;
            this.errorDialog = true;
            console.dir(error);
          });
      },
      newUser () {
        window.alert('Noop')
      },
      addProject () {
        this.projects.push({
          id: 0,
          name: 'project name',
          dateModified: 'Today',
          size: '0mb'
          });
      },
      create () {
        this.projects.push({
          id: 0,
          name: this.projectName,
          dateModified: 'Today',
          size: '0mb'
        });
      },
      searchOnTable () {
        this.searched = searchByName(this.projects, this.search)
      },
      toggleMenu () {
        this.menuVisible = !this.menuVisible
      },
      onSelect (item) {
         this.selected = item;
      },
             getAlternateLabel (count) {
               let plural = ''

               if (count > 1) {
                 plural = 's'
               }

               return `${count} user${plural} selected`
             }

      },

      created () {
        this.searched = this.projects
      },

      beforeCreate() {
            //if(localStorage.getItem("user") == null)
                //window.location.href = "/login";
      },
      mounted() {
        this.user = JSON.parse(localStorage.getItem("user"));
      }
});